package com.vkr.user_service.handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    /**
     * Время возникновения ошибки
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * Статус ошибки
     */
    private String url;

    /**
     * Сообщение об ошибке
     */
    private String message;

    /**
     * Конструктор
     *
     * @param e   исключение
     * @param url URL
     */
    public ErrorResponse(Throwable e, String url) {
        this.timestamp = LocalDateTime.now();
        this.message = e.getMessage();
        this.url = url;
    }
}
