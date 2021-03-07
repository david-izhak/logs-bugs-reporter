package telran.logs.bugs.api;

public interface LogsInfoApi {
//	String variables.
	String TYPE = "type";
	String N_TYPES = "n_types";
	String N_ARTIFACTS = "n_artifacts";
	
//	Paths for REST API.
	String LOGS = "/logs";
	String LOGS_TYPE = "/logs/type";
	String LOGS_EXCEPTIONS = "/logs/exceptions";
	String LOGS_DISTRIBUTION = "/logs/distribution/type";
	String LOGS_MOSTENCOUNTERED_EXCEPTION_TYPES = "/logs/mostencountered_exception_types";
	String LOGS_ARTIFACT_DISTRIBUTION = "/logs/artifacts_distribution";
	String LOGS_MOSTENCOUNTERED_ARTIFACTS = "/logs/mostencountered_artifacts";
	String MEDIATYPE_STREAM_JSON = "application/stream+json";
}
