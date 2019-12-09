package org.ngrinder.feature.model;

import java.util.Date;
import java.util.List;

/**
 * 创建压测
 */
public class TestPms {
	private Long id;
	private String name;//场景名
	private List<RequestPms> requestPmsList;//请求
	private List<FileData> fileDataList;//数据源
	private int ignoreSampleCount;//忽略取样数量
	private String targetHosts;//目标主机(需要在该主机安装monitor，检测执行机器的cpu、内存等)
	private Boolean useRampUp;//Ramp-Up可用 值为T或F
	private String rampUpType;//Ramp-Up类型，值为 process或thread
	private String threshold;//The threshold code, R for run count; D for duration
	private long duration;//测试持续时间，单位毫秒
	private int runCount;//测试次数，最大10000
	private int agentCount;//代理数，最小为1
	private int vuserPerAgent;//虚拟用户数量
	private int processes;//进程数
	private int rampUpInitCount;//Ramp-Up初始数
	private int rampUpInitSleepTime;//Ramp-Up初始等待时间,单位毫秒
	private int rampUpStep;//Ramp-Up增量
	private int rampUpIncrementInterval;//Ramp-Up进程增长间隔,单位毫秒
	private int threads;//线程数
	private int samplingInterval;//采样间隔
	private String param;//测试参数,测试脚本可以在命令行里获取参数，目前未实现'
	private Date scheduledTime;//定时时间，不在scenes表中
	private String userId;//创建者id

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(Date scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RequestPms> getRequestPmsList() {
		return requestPmsList;
	}

	public void setRequestPmsList(List<RequestPms> requestPmsList) {
		this.requestPmsList = requestPmsList;
	}

	public List<FileData> getFileDataList() {
		return fileDataList;
	}

	public void setFileDataList(List<FileData> fileDataList) {
		this.fileDataList = fileDataList;
	}

	public int getIgnoreSampleCount() {
		return ignoreSampleCount;
	}

	public void setIgnoreSampleCount(int ignoreSampleCount) {
		this.ignoreSampleCount = ignoreSampleCount;
	}

	public String getTargetHosts() {
		return targetHosts;
	}

	public void setTargetHosts(String targetHosts) {
		this.targetHosts = targetHosts;
	}

	public Boolean getUseRampUp() {
		return useRampUp;
	}

	public void setUseRampUp(Boolean useRampUp) {
		this.useRampUp = useRampUp;
	}

	public String getRampUpType() {
		return rampUpType;
	}

	public void setRampUpType(String rampUpType) {
		this.rampUpType = rampUpType;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getRunCount() {
		return runCount;
	}

	public void setRunCount(int runCount) {
		this.runCount = runCount;
	}

	public int getAgentCount() {
		return agentCount;
	}

	public void setAgentCount(int agentCount) {
		this.agentCount = agentCount;
	}

	public int getVuserPerAgent() {
		return vuserPerAgent;
	}

	public void setVuserPerAgent(int vuserPerAgent) {
		this.vuserPerAgent = vuserPerAgent;
	}

	public int getProcesses() {
		return processes;
	}

	public void setProcesses(int processes) {
		this.processes = processes;
	}

	public int getRampUpInitCount() {
		return rampUpInitCount;
	}

	public void setRampUpInitCount(int rampUpInitCount) {
		this.rampUpInitCount = rampUpInitCount;
	}

	public int getRampUpInitSleepTime() {
		return rampUpInitSleepTime;
	}

	public void setRampUpInitSleepTime(int rampUpInitSleepTime) {
		this.rampUpInitSleepTime = rampUpInitSleepTime;
	}

	public int getRampUpStep() {
		return rampUpStep;
	}

	public void setRampUpStep(int rampUpStep) {
		this.rampUpStep = rampUpStep;
	}

	public int getRampUpIncrementInterval() {
		return rampUpIncrementInterval;
	}

	public void setRampUpIncrementInterval(int rampUpIncrementInterval) {
		this.rampUpIncrementInterval = rampUpIncrementInterval;
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public int getSamplingInterval() {
		return samplingInterval;
	}

	public void setSamplingInterval(int samplingInterval) {
		this.samplingInterval = samplingInterval;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
