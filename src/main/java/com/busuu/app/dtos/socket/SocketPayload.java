package com.busuu.app.dtos.socket;

import com.busuu.app.entities.enums.TypeSocket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocketPayload<T> {
    private String messageId;
    private TypeSocket type;
    private T data;
    private Instant timestamp;
}
