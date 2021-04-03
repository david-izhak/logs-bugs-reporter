package telran.logs.bugs.api;

public interface ApiConstants {
	String URL_GET = "/accounts/{id}";
	String URL_GET_ACTIVATED = "/accounts/activated";
	String URL_POST = "/accounts";
	String URL_UPDATE_PASSWORD = "/accounts/update_password";
	String URL_NEW_ROLE = "/accounts/new_role";
	String URL_REMOV_ROLE = "/accounts/remov_role";
	String ID = "id";
	String ROLE_USER = "ROLE_USER";
	String ROLE_ADMIN = "ROLE_ADMIN";
	String USER = "USER";
	String ADMIN = "ADMIN";
	String DEVELOPER = "DEVELOPER";
	String TESTER = "TESTER";
	String ASSIGNER = "ASSIGNER";
	String PROJECT_OWNER = "PROJECT_OWNER";
	String TEAM_LEAD = "TEAM_LEAD";
	
	String USERNAME_PARAM = "username";
	
}
