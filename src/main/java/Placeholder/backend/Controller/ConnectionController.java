package Placeholder.backend.Controller;

import Placeholder.backend.DAO.ConnectionDAO;
import Placeholder.backend.Model.Connection;


import Placeholder.backend.Util.DAOFunctions;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


@RestController
public class ConnectionController {


    @PostMapping("/connection/createConnection")
    public Object createConnection(@RequestBody Connection connection){

        if(connection.getUser1_id() == 0 || connection.getUser2_id() == 0){
            return DAOFunctions.getResponse(400,"",null);
        }
        return DAOFunctions.getResponse(ConnectionDAO.createConnection(connection),"",null);
    }

    @GetMapping("/connection/checkConnection")
    public Object checkConnection(@RequestBody HashMap<String, String> body){

        if(!body.containsKey("user1_id") || !body.containsKey("user2_id") ||
                body.get("user1_id").equals("") || body.get("user2_id").equals("")){
            return DAOFunctions.getResponse(400,"",null);
        }
        return DAOFunctions.getResponse(200,"connected",ConnectionDAO.checkConnection(body.get("user1_id"),body.get("user2_id")));
    }

    @DeleteMapping("/connection/removeConnection")
    public Object removeConnection(@RequestBody HashMap<String, String> body){

        if(!body.containsKey("user1_id") || !body.containsKey("user2_id") ||
                body.get("user1_id").equals("") || body.get("user2_id").equals("")){
            return DAOFunctions.getResponse(400,"",null);
        }
        return DAOFunctions.getResponse(ConnectionDAO.removeConnection(body.get("user1_id"),body.get("user2_id")),"",null);

    }

    @DeleteMapping("/connection/deleteAllConnections")
    public Object removeAllConnections(@RequestBody HashMap<String, String> body){

        if(!body.containsKey("user1_id") || body.get("user1_id").equals("")){
            return DAOFunctions.getResponse(400,"",null);
        }
        return DAOFunctions.getResponse(ConnectionDAO.removeAllConnections(body.get("user1_id")),"",null);


    }

    @GetMapping("/connection/getAllConnections")
    public Object getAllRequests(){

        List<Connection> allConnections = ConnectionDAO.getAllConnections();
        if(allConnections != null){
            return DAOFunctions.getResponse(200,"allRequests",allConnections);
        }
        else{
            return DAOFunctions.getResponse(400,"",null);
        }
    }


}
