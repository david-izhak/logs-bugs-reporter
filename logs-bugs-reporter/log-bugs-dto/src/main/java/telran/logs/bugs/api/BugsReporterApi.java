package telran.logs.bugs.api;

public interface BugsReporterApi {
	static final String PATH_BUGS_PROGRAMMERS = "/bugs/programmers";
	static final String PATH_BUGS_OPEN = "/bugs/open";
	static final String PATH_BUGS_OPEN_ASSIGN = "/bugs/open/assign";
	static final String PATH_BUGS_ASSIGN = "/bugs/assign";
	static final String PATH_BUGS_PROGRAMMERS_GET = "/bugs/programmers";
	static final String BUGS_PROGRAMMERS_COUNT = "/bugs/programmers/count";
	static final String PATH_BUGS_ARTIFACT = "/bugs/artifact";
	static final String PATH_BUGS_CLOSE_DATA = "/bugs/close_bug";
	static final String BUGS_MOST_N_PROGRAMMERS = "/bugs/programmers/most";
	static final String BUGS_LEAST_N_PROGRAMMERS = "/bugs/programmers/least";
}
