package org.edu_sharing.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.edu_sharing.models.*;
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
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NBPLomPushService {


    private final WebClient edusharingOAIWebClient;
    private final WebClient nbpWebClient;


    @Value("${nbp.source.slug}")
    private String sourceSlug;

    private String sourceId;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");


    public Mono<Void> addOrUpdateNode(String nodeId) {
//        String uuid = "11";
        //language=xml
//        String testLom = String.format("""
//                  <lom xmlns="http://ltsc.ieee.org/xsd/LOM" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://ltsc.ieee.org/xsd/LOM  http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd">
//                  <general>
//                    <identifier>
//                      <catalog>local</catalog>
//                      <entry>%s</entry>
//                    </identifier>
//                    <title>
//                      <string language="en">Functions of a Complex Variable</string>
//                    </title>
//                    <language>en</language>
//                    <description>
//                      <string language="en">This is an advanced undergraduate course dealing with calculus in one complex variable with geometric emphasis. Since the course Analysis I (18.100B) is a prerequisite, topological notions like compactness, connectedness, and related properties of continuous functions are taken for granted.
//
//                        This course offers biweekly problem sets with solutions, two term tests and a final exam, all with solutions.</string>
//                    </description>
//                    <keyword>
//                      <string>Mathematics</string>
//                      <string>Topology and Geometry</string>
//                      <string>Calculus</string>
//                      <string language="en">Mathematical Analysis</string>
//                    </keyword>
//                    <structure/>
//                    <aggregationLevel/>
//                  </general>
//                  <lifeCycle>
//                    <version><string>1.1</string></version>
//                    <status/>
//                    <contribute>
//                      <role>
//                        <value>author</value>
//                      </role>
//                      <entity>BEGIN:VCARD
//                        VERSION:3.0
//                        FN:Massachusetts Institute of Technology
//                        N:;;;;
//                        ORG:Massachusetts Institute of Technology
//                        X-ROR:https://ror.org/042nb2s44
//                        END:VCARD</entity>
//                      <entity>BEGIN:VCARD
//                        VERSION:3.0
//                        FN:Sigurdur Helgason
//                        N:Helgason;Sigurdur;;;
//                        TITLE:Prof.
//                        END:VCARD</entity>
//                    </contribute>
//                  </lifeCycle>
//                  <metaMetadata/>
//                  <technical>
//                    <format>text/html</format>
//                    <location>https://repository.staging.openeduhub.net/edu-sharing/components/render/03dae343-96f3-4fa0-8f45-3cf55b885353</location>
//                  </technical>
//                  <educational>
//                    <learningResourceType>
//                      <value>lecture</value>
//                    </learningResourceType>
//                    <context>
//                      <value>higher education</value>
//                    </context>
//                    <intendedEndUserRole/>
//                  </educational>
//                  <rights>
//                    <copyrightAndOtherRestrictions>
//                      <value>yes</value>
//                    </copyrightAndOtherRestrictions>
//                    <description>
//                      <string language="de">https://creativecommons.org/licenses/by-nc-sa/4.0/deed.de</string>
//                    </description>
//                    <cost><value>no</value></cost>
//                  </rights>
//                  <classification>
//                    <purpose>
//                      <source>LOMv1.0</source>
//                      <value>discipline</value>
//                    </purpose>
//                    <keyword>
//                      <string language="de"/>
//                    </keyword>
//                    <taxonPath>
//                      <source>
//                        <string language="x-t-eaf">EAF Thesaurus</string>
//                      </source>
//                      <taxon>
//                        <id/>
//                      </taxon>
//                    </taxonPath>
//                  </classification>
//                </lom>
//                               """, uuid);

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
//        return Mono.just(testLom)
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
        return nbpWebClient.get()
                .uri(b -> b
                        .path("/datenraum/api/core/nodes/external/{sourceId}")
                        .queryParam("externalId", nodeId)
                        .build(sourceId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(NbpNodeDTO.class)
                .mapNotNull(HttpEntity::getBody)
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

}

