import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JobController implements Runnable{

    private static final Logger LOG = LoggerFactory.getLogger(JobController.class);

    private List<ClientRequest> requests = new ArrayList<>();

    private final List<FileJob> jobs;
    private final ExecutorService executorService;

    public JobController(List<FileJob> jobs) {
        this.jobs = jobs;
        executorService = Executors.newFixedThreadPool(jobs.size());
    }

    @Override
    public void run() {
        if (noRequests() || allJobsAreBusy()) {
            return;
        } else {
            ClientRequest request;
            synchronized (requests) {
                request = requests.remove(0);
            }
            FileJob job = getFirstFreeJob();
            job.setClientRequest(request);
            executorService.submit(job);
        }

    }

    synchronized public void addRequests(List<ClientRequest> requests){
        LOG.debug("Adding requests {}", requests.size());
        this.requests.addAll(requests);
        Collections.shuffle(requests);
    }

    private FileJob getFirstFreeJob() {
        return jobs.stream()
                .filter(fileJob -> !fileJob.isWorking())
                .findFirst()
                .orElseThrow(()->new ServerException("No free job found."));
    }

    private boolean noRequests(){
        return requests.isEmpty();
    }

    private boolean allJobsAreBusy(){
        return jobs.stream().allMatch(FileJob::isWorking);
    }
}
