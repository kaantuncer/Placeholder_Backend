package Placeholder.backend.DAO;

import Placeholder.backend.Model.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.*;

public class EventDAO {


    public static SessionFactory createFactory(){
        return new Configuration().
                configure("hibernate.cfg.xml").
                addAnnotatedClass(Event.class).addAnnotatedClass(Attend.class).addAnnotatedClass(User.class).
                buildSessionFactory();
    }

    private static void extractEventList( List<Object> result, List<Object> queryResult, boolean isMainFeed) {
        Gson gson = new Gson();
        if (queryResult.size() != 0) {
            JsonParser jsonParser = new JsonParser();

            HashSet<Integer> eventIdSet = new HashSet<>();

            Event prevEvent = null;
            User prevUser = null;
            List<User> currentParticipants = null;

            for(Object o : queryResult) {
                String jsonStr = gson.toJson(o);
                System.out.println(jsonStr);
                JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonStr);

                Event currentEvent = gson.fromJson(jsonArray.get(0), Event.class);
                User currentUser = gson.fromJson(jsonArray.get(1),User.class);

                if(!eventIdSet.contains(currentEvent.getId())){

                    if(prevEvent != null){
                        HashMap<String,Object> currentEventWithData = new HashMap<>();
                        currentEventWithData.put("event",prevEvent);
                        currentEventWithData.put("participants", currentParticipants);
                        currentEventWithData.put("user",prevUser);
                        result.add(currentEventWithData);
                    }

                    currentParticipants = new ArrayList<>();
                    prevEvent = currentEvent;
                    prevUser = currentUser;
                    eventIdSet.add(currentEvent.getId());
                }
            }


        }
    }

    public static Event createEvent(Event event){

        SessionFactory factory = createFactory();
        Session session = factory.getCurrentSession();
        List<Event> events;
        try{
            session.beginTransaction();
            session.save(event);
            events = session.createQuery(String.format("from Event e WHERE e.user_id = '%s' and e.event_share_date = '%s'",event.getUser_id(),event.getEvent_share_date())).getResultList();
            if(events.size() == 0){
                session.getTransaction().commit();
                return null;
            }
            session.getTransaction().commit();
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }
        finally {
            factory.close();
        }
        return events.get(events.size()-1);
    }


    public static int updateEvent(Event event){
        SessionFactory factory = createFactory();
        Session session = factory.getCurrentSession();

        try{
            session.beginTransaction();
            session.createQuery(String.format("update Event e SET e.event_body = '%s' , e.event_visual_data_path = '%s', e.event_location = '%s' WHERE e.id = '%s'",event.getEvent_body(),event.getEvent_visual_data_path(),event.getEvent_location(),event.getId())).executeUpdate();
            session.getTransaction().commit();
        }
        catch (Exception e){
            factory.close();
            System.out.println(e);
            return 400;
        }
        finally {
            factory.close();
        }

        return 200;

    }

    public static int deleteEvent(String eventId){

        SessionFactory factory = createFactory();
        Session session = factory.getCurrentSession();
        try{
            session.beginTransaction();
            session.createQuery("delete from Event e where e.id = "+eventId).executeUpdate();
            session.getTransaction().commit();
            EventDAO.deleteAllParticipantsFromAnEvent(eventId);
        }
        catch (Exception e){
            System.out.println(e);
            return 400;
        }
        finally {
            factory.close();
        }

        return 200;
    }

    public static List<Object> getAllEventsOfAUser(String user_id){
        SessionFactory factory = createFactory();
        Session session = factory.getCurrentSession();

        List<Object> result = new ArrayList<>();
        List<Object> queryResult;
        try{
            session.beginTransaction();
            queryResult = session.createQuery(String.format("from Event e " +
                    "INNER JOIN User u ON u.id = e.user_id " +
                    "LEFT JOIN Attend at ON e.id = at.event_id " +
                    "LEFT JOIN User cu ON at.user_id = cu.id WHERE e.user_id = '%s'",user_id)).getResultList();
            extractEventList(result, queryResult,false);
            session.getTransaction().commit();

        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }
        finally {
            factory.close();
        }

        return result;
    }

    public static int participateEvent(Attend attend){

        SessionFactory factory = createFactory();
        Session session = factory.getCurrentSession();

        try{
            session.beginTransaction();
            session.save(attend);
            session.getTransaction().commit();
        }
        catch (Exception e){
            factory.close();
            System.out.println(e);
            return 400;
        }
        finally {
            factory.close();
        }
        return 200;

    }

    public static int cancelParticipation(Attend attend){


        SessionFactory factory = createFactory();
        Session session = factory.getCurrentSession();

        try{
            session.beginTransaction();
            session.createQuery(String.format("delete from Attend at where at.user_id = '%s' and at.event_id = '%s' ",attend.getUser_id(),attend.getEvent_id())).executeUpdate();
            session.getTransaction().commit();
        }
        catch (Exception e){
            factory.close();
            System.out.println(e);
            return 400;
        }
        finally {
            factory.close();
        }

        return 200;
    }

    public static int deleteAllParticipantsFromAnEvent(String eventId){


        SessionFactory factory = createFactory();
        Session session = factory.getCurrentSession();

        try{
            session.beginTransaction();
            session.createQuery(String.format("delete from Attend at where at.event_id = '%s' ",eventId)).executeUpdate();
            session.getTransaction().commit();
        }
        catch (Exception e){
            factory.close();
            System.out.println(e);
            return 400;
        }
        finally {
            factory.close();
        }

        return 200;
    }

    public static List<Attend> getAllAttend(){
        SessionFactory factory = createFactory();
        Session session = factory.getCurrentSession();

        List<Attend> allAttend;
        try{
            session.beginTransaction();
            allAttend = session.createQuery("from Attend at").getResultList();
            session.getTransaction().commit();
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }
        finally {
            factory.close();
        }

        return allAttend;
    }






}
