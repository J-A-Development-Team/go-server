package JADevelopmentTeam.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

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
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
