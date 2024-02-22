package org.edu_sharing.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.edu_sharing.generated.repository.backend.services.rest.client.ApiException;
import org.edu_sharing.generated.repository.backend.services.rest.client.api.NodeV1Api;
import org.edu_sharing.generated.repository.backend.services.rest.client.model.Node;
import org.edu_sharing.generated.repository.backend.services.rest.client.model.NodeEntries;
import org.edu_sharing.generated.repository.backend.services.rest.client.model.Pagination;
import org.edu_sharing.models.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;


@Slf4j
@Service
@RequiredArgsConstructor
public class EduSharingService {

    private final NodeV1Api nodeV1Api;

    @Value("${edu.sharing.course.id}")
    private String collectionId;


    public static final String CCM_PRICE = "ccm:price";
    public static final String CCM_OEH_COURSE_COURSEMODE = "ccm:oeh_course_coursemode";
    public static final String CCLOM_GENERAL_DESCRIPTION = "cclom:general_description";
    public static final String CCLOM_GENERAL_LANGUAGE = "cclom:general_language";
    public static final String CCLOM_LOCATION = "cclom:location";
    public static final String CCM_LIFECYCLECONTRIBUTER_PUBLISHER = "ccm:lifecyclecontributer_publisher";

    private static final List<String> properties = Arrays.asList(
            CCM_PRICE,
            CCM_OEH_COURSE_COURSEMODE,
            CCLOM_GENERAL_DESCRIPTION,
            CCLOM_GENERAL_LANGUAGE,
            CCLOM_LOCATION,
            CCM_LIFECYCLECONTRIBUTER_PUBLISHER
    );

    private static final String HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
    private static final Pattern htmlPattern = Pattern.compile(HTML_PATTERN);

    public List<NBPCourseDTO> getCourse(String nodeId) throws ApiException {
        List<NBPCourseDTO> data = new ArrayList<>();
        Pagination pagination;
        int count = 0;
        do {
            NodeEntries children = nodeV1Api.getChildren("-home-", collectionId, 500, count, null, null, null, null, properties);
            List<NBPCourseDTO> birdData = children.getNodes().stream()
                    .map(this::map)
                    .filter(Objects::nonNull)
                    .toList();
            data.addAll(birdData);
            pagination = children.getPagination();
            count += pagination.getCount();
            log.info("processed: {} of {} nodes", count, pagination.getTotal());
        } while (count < pagination.getTotal());
        return data;
    }

    private NBPCourseDTO map(Node node) {
        return null;
//        Optional<Map<String, List<String>>> properties = Optional.ofNullable(node.getProperties());
//        try {
//            return new NBPDTO(
//                    properties.map(x -> x.get(CCM_PRICE)).flatMap(x -> x.stream().findFirst())
//                            .filter(x -> x.equals("http://w3id.org/openeduhub/vocabs/price/no"))
//                            .map(x -> CourseCharge.FREE)
//                            .map(I18N::new)
//                            .orElse(null),
//
//                    properties.map(x -> x.get(CCM_OEH_COURSE_COURSEMODE))
//                            .flatMap(x -> x.stream().findFirst())
//                            .map(x -> switch (x) {
//                                        case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/learningMode/selfPaced.html" ->
//                                                CourseMode.SELF_STUDY;
//                                        case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/learningMode/guided.html " ->
//                                                CourseMode.SUPERVISED;
//                                        default -> CourseMode.SELF_STUDY;
//                                    }
//                            )
//                            .map(I18N::new)
//                            .orElse(null),
//
//                    properties.map(x -> x.get(CCLOM_GENERAL_DESCRIPTION))
//                            .flatMap(x -> x.stream().findFirst())
//                            .map(I18N::new)
//                            .orElseThrow(() -> new NoSuchElementException("Missing cclom:general_description (long) ")),
//
//                    properties.map(x -> x.get(CCLOM_GENERAL_DESCRIPTION))
//                            .flatMap(x -> x.stream().findFirst())
//                            .map(x -> {
//                                try {
//                                    if (!htmlPattern.matcher(x).find()) {
//                                        return x.split("\n")[0];
//                                    }
//                                } catch (Exception ignore) {
//                                }
//                                return x;
//                            })
//                            .map(I18N::new)
//                            .orElseThrow(() -> new NoSuchElementException("Missing cclom:general_description (short)")),
//
//                    0L,
//
//                    new I18N<>(CourseTimeunit.MINUTE),
//
//                    properties.map(x -> x.get(CCLOM_GENERAL_LANGUAGE))
//                            .map(x -> x.stream()
//                                    .map(String::toLowerCase)
//                                    .map(CourseLanguage::fromString)
//                                    .filter(Objects::nonNull)
//                                    .toList())
//                            .map(x -> x.isEmpty() ? null : x)
//                            .map(I18N::new)
//                            .orElseThrow(() -> new NoSuchElementException("Missing cclom:general_language")),
//
//// Bad database for on OERSI so we use CourseLectureType.ONLINE_SELF_STUDY only
////                properties.map(x -> x.get("ccm:oeh_course_lecture_type"))
////                        .map(x -> x.stream()
////                                .map(y -> switch (y) {
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/learningFormat/presential.html" ->
////                                            CourseLectureType.PRESENCE;
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/learningFormat/online.html" ->
////                                            CourseLectureType.ONLINE_SELF_STUDY;
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/learningFormat/blended.html" ->
////                                            CourseLectureType.BLENDED_LEARNING;
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/learningFormat/online_appointments.html " ->
////                                            CourseLectureType.ONLINE_FIXED_GROUP_APPOINTMENTS;
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/learningFormat/mooc.html" ->
////                                            CourseLectureType.MASSIVE_OPEN_ONLINE_COURSE;
////                                    default -> null;
////                                })
////                                .filter(Objects::nonNull)
////                                .toList())
////                        .map(x -> x.isEmpty() ? null : x)
////                        .map(I18N::new)
////                        .orElse(new I18N<>(Collections.singletonList(CourseLectureType.ONLINE_SELF_STUDY))),
//                    new I18N<>(Collections.singletonList(CourseLectureType.ONLINE_SELF_STUDY)),
//                    node.getRef().getId(),
//
////                new I18N<>("WLO"),
//                    properties.map(x -> x.get(CCM_LIFECYCLECONTRIBUTER_PUBLISHER))
//                            .map(x -> x.stream()
//                                    .map(Ezvcard::parse)
//                                    .map(ChainingTextStringParser::all)
//                                    .flatMap(Collection::stream)
//                                    .map(VCard::getOrganization)
//                                    .filter(Objects::nonNull)
//                                    .map(ListProperty::getValues)
//                                    .flatMap(Collection::stream)
//                                    .filter(Objects::nonNull)
//                                    .toList())
//                            .map(I18N::new)
//                            .orElseThrow(() -> new NoSuchElementException("Missing ccm:lifecyclecontributer_publisher.ORG")),
//
//
//// Bad database of OERSI so we use null only
////                properties.map(x -> x.get("ccm:oeh_course_serviceprovider_url_image"))
////                        .flatMap(x -> x.stream().findFirst())
////                        .map(I18N::new)
////                        .orElse(new I18N<>("tbd.")),
//                    null,
//
//                    properties.map(x -> x.get(CCLOM_LOCATION))
//                            .flatMap(x -> x.stream().findFirst())
//                            .map(I18N::new)
//                            .orElseThrow(() -> new NoSuchElementException("Missing cclom:location")),
//
//// Bad database of OERSI so we use CourseTarget.STUDENTS only
////                properties.map(x -> x.get("ccm:oeh_course_targetgroup"))
////                        .map(x -> x.stream()
////                                .map(y -> switch (y) {
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/intendedEndUserRole_BIRD/pupil.html" ->
////                                            CourseTarget.PUPILS;
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/intendedEndUserRole_BIRD/prospective_student.html" ->
////                                            CourseTarget.PROSPECTIVE_STUDY;
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/intendedEndUserRole_BIRD/student.html " ->
////                                            CourseTarget.STUDENTS;
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/intendedEndUserRole_BIRD/prospective_doctoral.html  " ->
////                                            CourseTarget.PROSPECTIVE_DOCTORAL;
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/intendedEndUserRole_BIRD/pasch.html" ->
////                                            CourseTarget.PASCH;
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/intendedEndUserRole_BIRD/teacher.html" ->
////                                            CourseTarget.TEACHER;
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/intendedEndUserRole_BIRD/parent.html" ->
////                                            CourseTarget.PARENTS;
////                                    default -> null;
////                                })
////                                .filter(Objects::nonNull)
////                                .toList())
////                        .map(x->x.isEmpty() ? null : x)
////                        .map(I18N::new)
////                        .orElse(new I18N<>(Collections.singletonList(CourseTarget.STUDENTS))),
//                    new I18N<>(Collections.singletonList(CourseTarget.STUDENTS)),
//
//                    new I18N<>(Optional.of(node)
//                            .map(Node::getTitle)
//                            .orElseThrow(() -> new NoSuchElementException("Missing cclom:title"))),
//
//// Bad database of OERSI so we use CourseType.SPECIALIST_COURSE only
////                properties.map(x -> x.get("ccm:oeh_lrt"))
////                        .map(x -> x.stream()
////                                .map(y -> switch (y) {
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/new_lrt/8d5195dd-2e48-44d4-a9c1-6bccbf85ec96.html" ->
////                                            CourseType.LANGUAGE_COURSE;
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/new_lrt/8e157383-9ca3-4e20-849d-0881b648fd99.html" ->
////                                            CourseType.SPECIALIST_COURSE;
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/new_lrt/ff20ae9f-5d83-4f29-ba4f-993cbd743e5c.html" ->
////                                            CourseType.PROPAEDEUTICS;
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/new_lrt/89abe72e-d4c6-4797-ac36-175cfce25107.html" ->
////                                            CourseType.SOFT_SKILLS;
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/new_lrt/b0774ec7-49c0-49e0-8093-dce3ee6d02a0.html" ->
////                                            CourseType.BUSINESS_SKILLS;
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/new_lrt/4d64241b-3d8c-4d67-b9fe-9970f240d991.html" ->
////                                            CourseType.DIGITAL_SKILLS;
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/new_lrt/6f030e55-6193-4587-a374-d002cd43d787.html" ->
////                                            CourseType.ACADEMIC_SKILLS;
////                                    case "https://vocabs.openeduhub.de/w3id.org/openeduhub/vocabs/new_lrt/9ac858a9-fc06-41a1-a18e-faf7a1525198.html" ->
////                                            CourseType.CAREER_SKILLS;
////                                    default -> null;
////                                })
////                                .filter(Objects::nonNull)
////                                .toList())
////                        .map(x->x.isEmpty() ? null : x)
////                        .map(I18N::new)
////                        .orElse(new I18N<>(Collections.singletonList(CourseType.SPECIALIST_COURSE))),
//                    new I18N<>(Collections.singletonList(CourseType.SPECIALIST_COURSE)),
//
//// Bad database of OERSI so we use null only
//                    //new I18N<>(Optional.of(node).map(Node::getPreview).map(Preview::getUrl).orElse("tbd."))
//                    null
//            );
//        } catch (NoSuchElementException ex) {
//            log.warn("Node {} cause of: {}", node.getRef().getId(), ex.getMessage());
//            return null;
//        } catch (Exception ex) {
//            log.error("Error on Node {} caused by: {}", node.getRef().getId(), ex.getMessage(), ex);
//            return null;
//        }
    }
}
