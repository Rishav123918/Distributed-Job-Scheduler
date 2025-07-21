# Distributed-Job-Scheduler
🧠 PROJECT OVERVIEW
Your project is a Distributed Job Scheduler that:
Accepts concurrent job submissions.
Uses a thread pool to manage worker threads.
Distributes jobs to workers using Round-Robin load balancing.
Tracks job lifecycle: PENDING → RUNNING → COMPLETED.

📦 COMPONENT BREAKDOWN
1. Job Entity (Model)
Holds job data: name, parameters, status (PENDING, RUNNING, COMPLETED), timestamps, and assigned worker.

2. Job Controller
Accepts REST API requests:

/submit-multiple for submitting multiple jobs concurrently.

/api/jobs to retrieve job info.

3. Job Service
Business logic:
Saves jobs to the DB.
Handles job processing using @Async.
Assigns jobs to workers using Round-Robin.

4. Thread Pool Configuration
Limits how many concurrent jobs can run (e.g., pool size = 5).
Prevents the server from crashing under high load.
Each job is executed in a thread pulled from the pool.

🧵 WHAT IS A THREAD POOL & WHY WE USE IT?
✅ Problem:
If 100 users submit jobs at once, creating 100 threads is risky → high memory, CPU, and thread contention.

✅ Solution: Thread Pool
Thread pool = a fixed number of reusable threads (e.g., 5).
When a job is submitted:
A thread from the pool picks it up and runs it.
After finishing, the thread returns to the pool for reuse.

✅ Benefits:
Limits resource usage.
Improves scalability.
Prevents overload from too many parallel jobs.

🔁 ROUND-ROBIN WORKER ASSIGNMENT
✅ Why Round-Robin?
To balance load across workers evenly.

✅ How it works:
Maintain a list of workers: Worker-1, Worker-2, ..., Worker-N.
When a job is submitted:
Assign to the next worker in line.
If last worker is reached, cycle back to the first one.

Example:
Job-1 → Worker-1  
Job-2 → Worker-2  
Job-3 → Worker-3  
Job-4 → Worker-1  ← starts again
✅ In code:
This is usually handled by an AtomicInteger index and index % workers.size().

🔗 HOW THREAD POOL CONNECTS TO ROUND-ROBIN
Let’s connect the dots:

1. User Submits Jobs
API /submit-multiple is called with multiple jobs.

Each job is saved with status PENDING.

2. Thread Pool Executes Jobs
Each job is handed off to an @Async method.

Spring’s async task executor uses the thread pool to run it.

3. Round-Robin Assigns Worker
Inside the thread, job is assigned a worker (based on round-robin).

Status changed to RUNNING.

4. Job is Simulated
Thread sleeps (e.g., 2s) to mimic processing.

After that, status → COMPLETED.

🧩 VISUAL FLOW
          +--------------+          +----------------+
User ---> | JobController| -------> |  JobService    |
          +--------------+          +----------------+
                      ⬇
              Save jobs to DB
                      ⬇
                @Async method
                      ⬇
          +----------------------+
          | ThreadPoolExecutor   |  ← manages threads
          +----------------------+
                      ⬇
                Job Execution
                      ⬇
     +----------------------------+
     | Round Robin Worker Picker |
     +----------------------------+
                      ⬇
               Job status updated

