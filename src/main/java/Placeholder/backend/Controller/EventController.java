package Placeholder.backend.Controller;

import Placeholder.backend.DAO.EventDAO;
import Placeholder.backend.DAO.PostDAO;
import Placeholder.backend.Model.Attend;
import Placeholder.backend.Model.Event;
import Placeholder.backend.Model.Like;
import Placeholder.backend.Model.Post;
import Placeholder.backend.Util.DAOFunctions;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController

public class EventController {

    @GetMapping("/event/getAllEventsOfAUser")
    public Object getAllPostsOfAUser(@RequestParam(value = "user_id",defaultValue = "") String user_id){
        if(user_id.equals("")){
            return DAOFunctions.getResponse(400,"",null);
        }
        List<Object> res = EventDAO.getAllEventsOfAUser(user_id);
        if(res != null){
            return DAOFunctions.getResponse(200,"allEvents",res);
        }
        else{
            return DAOFunctions.getResponse(400,"",null);
        }
    }

    @PostMapping("/event/addEvent")
    public Object addEvent(@RequestBody Event event){

        if(event.getEvent_body() == null || event.getEvent_body().equals("")
            || event.getEvent_location() == null || event.getEvent_location().equals("")
            || event.getUser_id() == 0 || event.getEvent_share_date() == null || event.getEvent_share_date().equals("")){

            return DAOFunctions.getResponse(400,"error","Missing Fields");

        }

        Event e = EventDAO.createEvent(event);

        if(e == null) {
            return DAOFunctions.getResponse(400,"error","Database error");

        }
        return DAOFunctions.getResponse(200,"event",e);
    }

    @PatchMapping("/event/updateEvent")
    public Object updateEvent(@RequestBody Event event){
        if(event.getEvent_body() == null || event.getEvent_body().equals("") || event.getId() == 0 ||
            event.getEvent_location() == null ||event.getEvent_location().equals("")){
            return DAOFunctions.getResponse(400,"error","Missing Fields");
        }
        return DAOFunctions.getResponse(EventDAO.updateEvent(event),"",null);
    }

    @DeleteMapping("/event/deleteEvent")
    public Object deleteEvent(@RequestBody HashMap<String, String> body){
        if(!body.containsKey("id") || body.get("id").equals("")){
            return DAOFunctions.getResponse(400,"",null);
        }
        return DAOFunctions.getResponse(EventDAO.deleteEvent(body.get("id")),"",null);
    }

    @PostMapping("/event/participateEvent")
    public Object participateEvent(@RequestBody Attend attend){

        if(attend.getUser_id() == 0 || attend.getEvent_id() == 0){

            return DAOFunctions.getResponse(400,"error","Missing Fields");

        }
        return DAOFunctions.getResponse(EventDAO.participateEvent(attend),"",null);
    }

    @DeleteMapping("/event/cancelParticipation")
    public Object cancelParticipation(@RequestBody Attend attend){

        if(attend.getUser_id() == 0 || attend.getEvent_id() == 0){

            return DAOFunctions.getResponse(400,"error","Missing Fields");

        }
        return DAOFunctions.getResponse(EventDAO.cancelParticipation(attend),"",null);
    }

    @GetMapping("/event/getAllAttend")
    public Object getAllAttend(){

        List<Attend> allAttend = EventDAO.getAllAttend();

        if(allAttend != null){
            return DAOFunctions.getResponse(200,"allAttend",allAttend);

        }
        else{
            return DAOFunctions.getResponse(400,"",null);
        }

    }
}
