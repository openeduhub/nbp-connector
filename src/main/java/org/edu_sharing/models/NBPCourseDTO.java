package org.edu_sharing.models;

import java.time.LocalDateTime;

public record NBPCourseDTO(
   String title,
   String description,
   String uri,
   double cost,
   String address,
   String latitude,
   String longitude,
   String startDate,
   String endDate
//   LocalDateTime startDate,
//   LocalDateTime endDate
){

}

