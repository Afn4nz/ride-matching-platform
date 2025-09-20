package com.ridematch.driver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileDTO {
    private String name;
    private String lastModified;
    private String url;
}
