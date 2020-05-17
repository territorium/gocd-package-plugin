
package info.tol.gocd.task;

import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import info.tol.gocd.task.qt.PackageConfig;
import info.tol.gocd.task.qt.PackageExecutor;
import info.tol.gocd.test.TaskRequestBuilder;
import info.tol.gocd.test.TestLogger;
import info.tol.gocd.util.Environment;


public class TaskExecutorTest2 {

  private static final File        workingDir  = new File("/home/brigl/test");
  private static final Environment environment = new Environment();

  @Test
  public void toolsLinuxTest() {
    TaskRequestBuilder request = TaskRequestBuilder.of(workingDir, environment);
    request.set(PackageConfig.NAME, "1.0");
    request.set(PackageConfig.PATH, "packages");
    request.set(PackageConfig.MODULE, "tol.tools_linux");
    request.set(PackageConfig.SOURCE,
        "download/tools-qt-5.12.4-gcc.tar.gz\ndownload/monitor-linux-(?<VERSION>[0-9.\\-+]+).tar.gz;bin");
    request.set(PackageConfig.TARGET, "tools");

    PackageExecutor handler = new PackageExecutor(TestLogger.CONSOLE);
    GoPluginApiResponse response = handler.handle(request.build());
    if (response.responseCode() != DefaultGoApiResponse.SUCCESS_RESPONSE_CODE) {
      Assert.fail(response.responseBody());
    }
  }
}
