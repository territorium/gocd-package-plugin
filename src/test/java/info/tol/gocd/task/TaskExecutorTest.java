
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


public class TaskExecutorTest {

  private static final File        workingDir  = new File("/home/brigl/test");
  private static final Environment environment = new Environment();

  @Test
  public void vcTest() {
    TaskRequestBuilder request = TaskRequestBuilder.of(workingDir, environment);
    request.set(PackageConfig.NAME, "1.0");
    request.set(PackageConfig.PATH, "packages");
    request.set(PackageConfig.MODULE, "com.microsoft.vcredist");
    request.set(PackageConfig.SOURCE, "download/VC_redist.x64.exe");
    request.set(PackageConfig.TARGET, "");

    PackageExecutor handler = new PackageExecutor(TestLogger.CONSOLE);
    GoPluginApiResponse response = handler.handle(request.build());
    if (response.responseCode() != DefaultGoApiResponse.SUCCESS_RESPONSE_CODE) {
      Assert.fail(response.responseBody());
    }
  }

  @Test
  public void serverLinuxTest() {
    TaskRequestBuilder request = TaskRequestBuilder.of(workingDir, environment);
    request.set(PackageConfig.NAME, "20.04-dev");
    request.set(PackageConfig.PATH, "installer/packages2");
    request.set(PackageConfig.MODULE, "tol./smartio/$RELEASE/.server_linux");
    request.set(PackageConfig.SOURCE, "download/smartIO-Server-Linux-(?<VERSION>[\\d.\\-+]+).tar.gz");
    request.set(PackageConfig.TARGET, "");

    PackageExecutor handler = new PackageExecutor(TestLogger.CONSOLE);
    GoPluginApiResponse response = handler.handle(request.build());
    if (response.responseCode() != DefaultGoApiResponse.SUCCESS_RESPONSE_CODE) {
      Assert.fail(response.responseBody());
    }
  }

  @Test
  public void toolsWin64Test() {
    TaskRequestBuilder request = TaskRequestBuilder.of(workingDir, environment);
    request.set(PackageConfig.NAME, "1.0");
    request.set(PackageConfig.PATH, "packages");
    request.set(PackageConfig.MODULE, "tol.tools_win64");
    request.set(PackageConfig.SOURCE,
        "download/tools-qt-5.12.4-msvc.zip\ndownload/monitor-win64-(?<VERSION>[0-9.\\-+]+).zip");
    request.set(PackageConfig.TARGET, "tools");

    PackageExecutor handler = new PackageExecutor(TestLogger.CONSOLE);
    GoPluginApiResponse response = handler.handle(request.build());
    if (response.responseCode() != DefaultGoApiResponse.SUCCESS_RESPONSE_CODE) {
      Assert.fail(response.responseBody());
    }
  }

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

  @Test
  public void clientLinuxTest() {
    TaskRequestBuilder request = TaskRequestBuilder.of(workingDir, environment);
    request.set(PackageConfig.NAME, "20.04-dev");
    request.set(PackageConfig.PATH, "installer/packages2");
    request.set(PackageConfig.MODULE, "tol./smartio/$RELEASE/.app.linux");
    request.set(PackageConfig.SOURCE,
        "download/smartIO-Linux-(?<VERSION>[0-9.\\-+]+).tar.gz\ndownload/Qt-5.12.4-gcc.tar.gz");
    request.set(PackageConfig.TARGET, "20.04-dev/client");

    PackageExecutor handler = new PackageExecutor(TestLogger.CONSOLE);
    GoPluginApiResponse response = handler.handle(request.build());
    if (response.responseCode() != DefaultGoApiResponse.SUCCESS_RESPONSE_CODE) {
      Assert.fail(response.responseBody());
    }
  }
}
