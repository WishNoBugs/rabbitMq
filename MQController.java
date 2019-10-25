import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/mlm/mq")
public class MQController {

    private static final Logger log = LoggerFactory.getLogger(RabbitmqConfig.class);

    @Autowired
    private Environment env;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/writeToMq")
    public ResultVO writeToMq(@RequestParam String message,@RequestParam String sign,@RequestParam String paramTime, HttpServletRequest req){
        ResultVO response=new ResultVO();
//        try {
//            response = SecurityUtil.checkIPWhite(req.getRemoteAddr());
//            if(!response.isSuccess()) return response;
//            response = SecurityUtil.checkSign(sign,paramTime,SLAT);
//            if(!response.isSuccess()) return response;
//        } catch (Exception e) {
//            log.error("",e);
//            e.printStackTrace();
//            //未知错误
//            response.setSuccess(false);
//            response.setMessage(MsgEnum.UNKNOWN_ERROR);
//        }
        if(message == null){
            response.setSuccess(false);
            response.setMessage(MsgEnum.PARAMS_IS_NULL);
            return response;
        }
        rabbitTemplate.convertAndSend(env.getProperty("mail.queue.name"), message);
        response.setSuccess(true);
        response.setMessage(MsgEnum.ADD_SECCESS);
        return response;
    }

    @GetMapping("/send")
    public String send() {
        String msg="hello-001";
        System.out.println("发送消息:"+ LocalDateTime.now().toString()+" 内容："+msg);
        rabbitTemplate.convertAndSend("enqueQueue", msg);
        return "ok";
    }
}
