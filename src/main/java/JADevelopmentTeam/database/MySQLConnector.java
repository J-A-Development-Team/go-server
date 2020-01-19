package JADevelopmentTeam.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.math.BigInteger;

public abstract class MySQLConnector {
    private static SessionFactory sessionFactory;
    public static SessionFactory getSessionFactory(){
        if (sessionFactory == null) {
            synchronized (MySQLConnector.class) {
                if (sessionFactory == null) {
                    sessionFactory = new Configuration()
                            .configure() // configures settings from hibernate.cfg.xml
                            .buildSessionFactory();
                }
            }
        }
        return sessionFactory;
    }
    public static void sendObject(Object object){
        try {
            SessionFactory sessionFactory = getSessionFactory();
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(object);
            session.getTransaction().commit();
            session.close();
            if (object instanceof Move){
                Move move =(Move) object;
                move.makeOneStep();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public static int getLastGameID(){
        try {
            SessionFactory sessionFactory = getSessionFactory();
            Session session = sessionFactory.openSession();
            return ((BigInteger) session.createSQLQuery("SELECT last_insert_id() from game limit 1;").uniqueResult()).intValue();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }
}
