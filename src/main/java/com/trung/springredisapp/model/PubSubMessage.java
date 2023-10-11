package com.trung.springredisapp.model;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Getter
@Setter
public class PubSubMessage<T> {
    private String type;
    private T data;
}