package com.sgbit.ara.DAO;

import java.util.List;
import java.util.Iterator;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.sgbit.ara.model.User;

public class UserDAO {
	public int registerUser(User user) {
	SessionFactory sessionFactory = null;
	Session session=null;
	try {
		 sessionFactory=new Configuration().configure().buildSessionFactory();
		session=sessionFactory.openSession();
		Transaction transaction=session.beginTransaction();
		session.save(user);
		transaction.commit();
		
		return 1;
		
		
	}
	catch(Exception e) {
		e.printStackTrace();
	}
	finally {
		if( sessionFactory != null) {
			sessionFactory.close();
			
			
			
		}
		if(session != null) {
			session.close();
			
		}
	}
	return 0;
	}
	 public List<User> listusers( ){
		 SessionFactory sessionFactory = null;
			Session session=null;
			try {
				 sessionFactory=new Configuration().configure().buildSessionFactory();
				session=sessionFactory.openSession();
				Transaction transaction=session.beginTransaction();

				Query query = session.createQuery("from User");
				List<User> userList = query.list();
				
			        transaction.commit();
				
				return userList;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			finally {
				if( sessionFactory != null) {
					sessionFactory.close();
					
					
					
				}
				if(session != null) {
					session.close();
					
				}
			}
			 return null;
	     
	      }
	 
	 
	 public User validateUser(String userId, String password ){
		 SessionFactory sessionFactory = null;
			Session session=null;
			try {
				 sessionFactory=new Configuration().configure().buildSessionFactory();
				session=sessionFactory.openSession();
				Transaction transaction=session.beginTransaction();

				Query query = session.createQuery("from User where mobileNo = :mobileNo and password = :password");
				query.setParameter("mobileNo", userId);
				query.setParameter("password", password);
				List<User> userList = query.list();
				
			        transaction.commit();
				if(userList!=null & userList.size()>0) {
					return userList.get(0);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			finally {
				if( sessionFactory != null) {
					sessionFactory.close();
				}
				if(session != null) {
					session.close();
					
				}
			}
			 return null;
	     
	      }
	
}