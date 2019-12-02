package org.ngrinder.feature.utils;

import org.apache.commons.lang3.StringUtils;
import org.ngrinder.feature.model.TestPms;
import org.ngrinder.model.PerfTest;
import org.ngrinder.model.RampUp;

public class BeanCovertUtils {

	public static PerfTest getPerfTestBean(TestPms testPms) {
		PerfTest perfTest = new PerfTest();
		perfTest.setTestName(testPms.getName());
		perfTest.setIgnoreSampleCount(testPms.getIgnoreSampleCount());
		perfTest.setTargetHosts(testPms.getTargetHosts());
		perfTest.setUseRampUp(testPms.getUseRampUp());
		if (StringUtils.isEmpty(testPms.getRampUpType())) {
			testPms.setRampUpType(RampUp.PROCESS.name());
		}
		perfTest.setRampUpType(RampUp.valueOf(testPms.getRampUpType()));
		perfTest.setThreshold(testPms.getThreshold());
		perfTest.setDuration(testPms.getDuration());
		perfTest.setRunCount(testPms.getRunCount());
		perfTest.setAgentCount(testPms.getAgentCount());
		perfTest.setVuserPerAgent(testPms.getVuserPerAgent());
		perfTest.setProcesses(testPms.getProcesses());
		perfTest.setRampUpInitCount(testPms.getRampUpInitCount());
		perfTest.setRampUpInitSleepTime(testPms.getRampUpInitSleepTime());
		perfTest.setRampUpStep(testPms.getRampUpStep());
		perfTest.setRampUpIncrementInterval(testPms.getRampUpIncrementInterval());
		perfTest.setThreads(testPms.getThreads());
		perfTest.setSamplingInterval(testPms.getSamplingInterval());
		perfTest.setParam(testPms.getParam());
		return perfTest;
	}
}
