package ufrr.editora.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import ufrr.editora.entity.Usuario;

//@author Macksuel Lopes

public class UsuarioDAO {
	
	public Usuario existe(Usuario usuario) {
		EntityManager em = new JPAUtil().getEntityManager();
		em.getTransaction().begin();
		
//		Query query = em.createQuery("from Usuario u where u.login = " + ":pLogin and u.senha = MD5(:pSenha)");
		Query query = em.createQuery("from Usuario u where u.login = " + ":pLogin and u.senha = (:pSenha)");
		query.setParameter("pLogin", usuario.getLogin());
		query.setParameter("pSenha", usuario.getSenha());
		
		Usuario login = new Usuario();
		if(!query.getResultList().isEmpty()){
			login = (Usuario) query.getSingleResult();
//			if(login.isStatus()){
//				System.out.println("Usuário Logado");
//			}

		} else {
			login = null;
		}
		
		em.getTransaction().commit();
		em.close();
		
		return login;
	}
	
//	public Usuario esqueciSenha(Usuario funcionario){
//		EntityManager em = new JPAUtil().getEntityManager();
//		em.getTransaction().begin();
//		
//		Query query = em.createQuery("from Funcionario u where u.login = " + ":pLogin and u.campo_secreto = MD5(:pSecreto)");
//		query.setParameter("pLogin", funcionario.getLogin());
//		query.setParameter("pSecreto", funcionario.getCampo_secreto());
//		
//		Funcionario funcLogin = new Funcionario();
//		if(!query.getResultList().isEmpty()){
//			funcLogin = (Funcionario) query.getSingleResult();
//		} else {
//			funcLogin = null;
//			// Ver o que realizar aqui
//		}
//		
//		em.getTransaction().commit();
//		em.close();
//		
//		return funcLogin;
//	}

}
