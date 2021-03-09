package telran.logs.bugs.api;

public interface BugsReporterApi {
	String PATH_BUGS_PROGRAMMERS = "/bugs/programmers";
	String PATH_BUGS_OPEN = "/bugs/open";
	String PATH_BUGS_OPEN_ASSIGN = "/bugs/open/assign";
	String PATH_BUGS_ASSIGN = "/bugs/assign";
	String PATH_BUGS_PROGRAMMERS_GET = "/bugs/programmers";
	String BUGS_PROGRAMMERS_COUNT = "/bugs/programmers/count";
	String PATH_BUGS_ARTIFACT = "/bugs/artifact";
	String PATH_BUGS_CLOSE_DATA = "/bugs/close_bug";
	String BUGS_MOST_N_PROGRAMMERS = "/bugs/programmers/most";
	String BUGS_LEAST_N_PROGRAMMERS = "/bugs/programmers/least";
	String BUGS_SERIOUSNESS_COUNT = "/bugs/seriousness/count";
	String BUGS_SERIOUSNESS_MOST = "/bugs/seriousness/most";
	String BUGS_WITH_DURATIONS = "/bugs/unclosed";
	String BUGS_NONASSIGNED = "/bugs/nonassigned";
}
