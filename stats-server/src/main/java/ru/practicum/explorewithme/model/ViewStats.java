package ru.practicum.explorewithme.model;

import lombok.Value;

@Value
public class ViewStats {

    String app;

    String uri;

    Long hits;
}
