package com.muchq.holler;

import com.muchq.holler.core.HttpRequest;
import com.muchq.holler.core.HttpResponse;
import com.muchq.holler.model.Widget;
import com.muchq.holler.resource.TestResource;
import com.muchq.holler.util.RequestUtils;
import com.muchq.lunarcat.Service;
import com.muchq.lunarcat.Service.ServerMode;
import com.muchq.lunarcat.config.Configuration;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.net.ServerSocket;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

public class AsyncHttpTest {
  private static Service service;
  private final static HttpClient client = new AsyncHttp(new DefaultAsyncHttpClient());
  private static String baseUrl;

  @BeforeClass
  public static void setup() {
    int port = getPort();
    baseUrl = "http://localhost:" + port;
    Configuration configuration = Configuration.newBuilder()
        .withPort(port)
        .withBasePackage(TestResource.class.getPackage())
        .build();
    service = new Service(configuration);
    service.run(ServerMode.NO_WAIT);
  }

  @AfterClass
  public static void tearDown() {
    service.shutDown();
  }

  @Test
  public void itCanMakeGetRequests() throws Exception {
    String idParam = "1";
    String nameParam = "Tippy";

    HttpRequest request = HttpRequest.builder()
        .setUrl(baseUrl + "/test/" + idParam)
        .addQueryParams(RequestUtils.param("name", nameParam))
        .build();

    HttpResponse response = client.execute(request).get();

    Widget widget = response.getAs(Widget.class);
    Widget expected = Widget.builder().setId(Integer.valueOf(idParam)).setName(nameParam).build();
    assertThat(widget).isEqualTo(expected);
  }

  @Test
  public void itCanMakePostRequests() throws Exception {
    Widget widget = getWidget();

    HttpRequest request = HttpRequest.builder()
        .setUrl(baseUrl + "/test")
        .setMethod("POST")
        .setBody(widget)
        .build();

    HttpResponse response = client.execute(request).get();

    Widget read = response.getAs(Widget.class);
    assertThat(response.getContentType()).isPresent().isEqualTo(Optional.of(MediaType.APPLICATION_JSON));
    Widget expected = Widget.builder().setId(widget.getId() * -1).setName(widget.getName().toUpperCase()).build();
    assertThat(read).isEqualTo(expected);
  }

  @Test
  public void itCanMakePutRequests() throws Exception {
    Widget widget = getWidget();
    HttpRequest request = HttpRequest.builder()
        .setUrl(baseUrl + "/test")
        .setMethod("PUT")
        .setBody(widget)
        .build();

    Widget read = client.execute(request).get().getAs(Widget.class);
    assertThat(read).isEqualTo(widget);
  }

  @Test
  public void itCanMakeDeleteRequests() throws Exception {
    HttpRequest request = HttpRequest.builder()
        .setUrl(baseUrl + "/test/1")
        .setMethod("DELETE")
        .build();

    HttpResponse response = client.execute(request).get();
    assertThat(response.getStatusCode()).isEqualTo(204);
  }

  private Widget getWidget() {
    return Widget.builder()
        .setId(ThreadLocalRandom.current().nextInt())
        .setName("foo")
        .build();
  }

  private static int getPort() {
    try {
      ServerSocket serverSocket = new ServerSocket(0);
      int port = serverSocket.getLocalPort();
      serverSocket.close();
      return port;
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }
}
