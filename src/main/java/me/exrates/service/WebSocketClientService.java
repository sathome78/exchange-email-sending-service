package me.exrates.service;

import me.exrates.model.StatusModel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketClientService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketClientService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    public void sendStatusOk(StatusModel statusModel) {
        messagingTemplate.convertAndSend("/topic/email_status", statusModel);
    }
}
