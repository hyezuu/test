package com.ssdam.reply.controller;

import com.ssdam.dto.SingleResponseDto;
import com.ssdam.reply.dto.ReplyDto;
import com.ssdam.reply.entity.Reply;
import com.ssdam.reply.mapper.ReplyMapper;
import com.ssdam.reply.service.ReplyService;
import com.ssdam.utils.UriCreator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;

@RestController
@RequestMapping("/v1/replies")
public class ReplyController {
    private final static String REPLY_DEFAULT_URL = "/v1/replies";
    private final ReplyService replyService;
    private final ReplyMapper mapper;

    public ReplyController(ReplyService replyService, ReplyMapper mapper) {
        this.replyService = replyService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postReply(@RequestBody @Valid ReplyDto.Post requestBody){
        Reply reply = mapper.replyPostDtoToReply(requestBody);
        Reply createdReply = replyService.createReply(reply);

        URI location = UriCreator.createUri(REPLY_DEFAULT_URL, createdReply.getReplyId());
        return ResponseEntity.created(location).build();
    }
    @PatchMapping("/{reply-id}")
    public ResponseEntity patchReply(@PathVariable("reply-id") @Positive long replyId,
                                     @RequestBody @Valid ReplyDto.Patch requestBody){
        requestBody.setReplyId(replyId);
        Reply reply = mapper.replyPatchDtoToReply(requestBody);
        Reply updatedReply = replyService.updateReply(reply);

        return new ResponseEntity(
                new SingleResponseDto<>(mapper.replyToReplyResponse(updatedReply)),
                HttpStatus.OK);
    }
    @GetMapping("/{reply-id}")
    public ResponseEntity getReply(@PathVariable("reply-id") @Positive long replyId){
        Reply reply = replyService.findReply(replyId);

        return new ResponseEntity(
                new SingleResponseDto<>(mapper.replyToReplyResponse(reply)),
                HttpStatus.OK);
    }
    @DeleteMapping("/{reply-id}")
    public ResponseEntity deleteReply(@PathVariable("reply-id") @Positive long replyId){
        replyService.deleteReply(replyId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
