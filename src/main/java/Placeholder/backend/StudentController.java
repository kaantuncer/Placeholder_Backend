package Placeholder.backend;



import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StudentController {

    // /greeting?name=Dan
    @GetMapping("/getStudent")
    public Student getStudent(@RequestParam(value = "id",defaultValue = "") String id){

        if(id.equals("")){
            return null;
        }


        SessionFactory factory = new Configuration().
                configure("hibernate.cfg.xml").
                addAnnotatedClass(Student.class).buildSessionFactory();

        Session session = factory.getCurrentSession();

        Student s = null;

        try{
            session.beginTransaction();
            List<Student> allStudents = session.createQuery("from Student s WHERE s.id = "+id).list();
            System.out.println(allStudents);
            session.getTransaction().commit();
            if(allStudents.size() > 0){
                s = allStudents.get(0);
            }

        }
        finally {
            factory.close();
        }

        return s;
    }

    @PostMapping("/createStudent")
    public int createStudent(@RequestBody Student student){

        SessionFactory factory = new Configuration().
                configure("hibernate.cfg.xml").
                addAnnotatedClass(Student.class).
                buildSessionFactory();

        Session session = factory.getCurrentSession();

        try{


            session.beginTransaction();

            session.save(student);

            session.getTransaction().commit();

        }
        catch (Exception e){
            return 400;
        }
        finally {
            factory.close();
        }


        return 200;
    }

    @DeleteMapping("/deleteStudent")
    public int deleteStudent(@RequestParam(value = "id") String id){

        SessionFactory factory = new Configuration().
                configure("hibernate.cfg.xml").
                addAnnotatedClass(Student.class).
                buildSessionFactory();

        Session session = factory.getCurrentSession();

        try{
            session.beginTransaction();
            session.createQuery("delete from Student s where s.id = "+id).executeUpdate();
            session.getTransaction().commit();

        }
        catch (Exception e){
            return 400;
        }
        finally {
            factory.close();
        }

        return 200;


    }

    @PatchMapping("/updateStudent")
    public String updateStudent(@RequestBody Student student){
        SessionFactory factory = new Configuration().
                configure("hibernate.cfg.xml").
                addAnnotatedClass(Student.class).
                buildSessionFactory();

        Session session = factory.getCurrentSession();

        try{
            session.beginTransaction();
            session.createQuery(String.format("update Student s SET s.firstName = '%s' , s.lastName = '%s' , s.email = '%s' WHERE s.id = '%s' ",student.getFirstName(),student.getLastName(),student.getEmail(),student.getId())).executeUpdate();
            session.getTransaction().commit();

        }
        catch (Exception e){
            factory.close();
            return e.toString();
        }
        finally {
            factory.close();
        }
        return "200";
    }

}
