package org.edu_sharing.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.edu_sharing.models.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import reactor.core.publisher.Mono;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.io.StringWriter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NBPLomPushService {


    private final WebClient edusharingOAIWebClient;
    private final WebClient nbpWebClient;


    @Value("${nbp.source.slug}")
    private String sourceSlug;

    private String sourceId;



    public Mono<String> getNode(String nodeId) {
        return retrieveSourceId()
                .flatMap(x->retrieveRawNode(nodeId));
    }


    public Mono<Void> addOrUpdateNode(String nodeId) {
        return edusharingOAIWebClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("verb", "GetRecord").queryParam("metadataPrefix", "lom").queryParam("identifier", nodeId).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .<String>handle((x, sink) -> {
                    try {
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        Document doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(x)));

                        XPath xPath = XPathFactory.newInstance().newXPath();
                        Node result = (Node) xPath.evaluate("OAI-PMH/GetRecord/record/metadata/lom", doc, XPathConstants.NODE);

                        StringWriter buf = new StringWriter();
                        Transformer xform = TransformerFactory.newInstance().newTransformer();
                        xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                        xform.transform(new DOMSource(result), new StreamResult(buf));
                        sink.next((buf.toString()));
                    } catch (Exception ex) {
                        sink.error(new RuntimeException(ex));
                    }
                })
                .flatMap(lomXml -> retrieveSourceId()
                        .flatMap(x -> retrieveIdFor(nodeId))
                        .switchIfEmpty(Mono.defer(() -> createByLom(lomXml).then(Mono.empty())))
                        .flatMap(y -> updateWithLom(y, lomXml))

                ).onErrorResume(e -> {
                    if (e instanceof WebClientResponseException) {
                        String responseBodyAs = ((WebClientResponseException) e).getResponseBodyAs(String.class);
                        log.error("{}: {}", e.getMessage(), responseBodyAs);
                    } else {
                        log.error("{}", e.getMessage());
                    }
                    return Mono.error(e);
                });
    }

    public Mono<Void> deleteNode(String nodeId){
        return retrieveSourceId()
                .flatMap(x->retrieveIdFor(nodeId))
                .flatMap(this::deleteById);
    }

    private Mono<Void> deleteById(String id) {
        return nbpWebClient.delete()
                .uri("/datenraum/api/core/nodes/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }

    private Mono<String> retrieveSourceId() {
        return Mono.justOrEmpty(sourceId)
                .switchIfEmpty(nbpWebClient.get()
                        .uri("/datenraum/api/core/sources/slug/{slug}", sourceSlug)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(NbpSourceDTO.class)
                        .map(NbpSourceDTO::id)
                        .doOnSuccess(x -> sourceId = x));
    }

    private Mono<Void> updateWithLom(String id, String lomXml) {
        return nbpWebClient.put()
                .uri("/push-connector/api/lom/{id}", id)
                .bodyValue(new NbpLomDTO(null, lomXml))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Void.class);
    }

    private Mono<Void> createByLom(String lomXml) {
        return nbpWebClient.post()
                .uri("/push-connector/api/lom")
                .bodyValue(new NbpLomDTO(sourceId, lomXml))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Void.class);
    }


    private Mono<String> retrieveIdFor(String nodeId) {
        return retrieveNode(nodeId)
                .map(NbpNodeDTO::id)
                .onErrorResume(e -> {
                    if (e instanceof WebClientResponseException resp) {
                        if (resp.getStatusCode() == HttpStatus.NOT_FOUND) {
                            return Mono.empty();
                        }
                    }
                    return Mono.error(e);
                });
    }
    @NotNull
    private Mono<String> retrieveRawNode(String nodeId) {
        return nbpWebClient.get()
                .uri(b -> b
                        .path("/datenraum/api/core/nodes/external/{sourceId}")
                        .queryParam("externalId", nodeId)
                        .build(sourceId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class)
                .mapNotNull(HttpEntity::getBody);
    }

    @NotNull
    private Mono<NbpNodeDTO> retrieveNode(String nodeId) {
        return nbpWebClient.get()
                .uri(b -> b
                        .path("/datenraum/api/core/nodes/external/{sourceId}")
                        .queryParam("externalId", nodeId)
                        .build(sourceId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(NbpNodeDTO.class)
                .mapNotNull(HttpEntity::getBody);
    }
}

