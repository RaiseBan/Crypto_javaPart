package ipProxy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.*;
import java.lang.reflect.Type;
public class Proxy {

    public List<String> getProxy(String url){
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<String> proxy = new ArrayList<>();
        Elements rows = doc.select("table.table-striped tbody tr");
        for (Element row : rows) {
            Elements tds = row.select("td");
            try {
                String ip = tds.get(0).text().trim();
                String port = tds.get(1).text().trim();
                String ssl = tds.get(6).text().trim();
                if (ssl.equals("yes")) {
                    String host = ip + ":" + port;
                    proxy.add(host);
                }
            } catch (IndexOutOfBoundsException e) {
                continue;
            }
        }
        return proxy;
    }
    public void testProxy(){
        for (int i = 0; i < 4; i++) {
            Document document = null;
            try {
                document = Jsoup.connect("http://icanhazip.com").timeout(1500).get();
                System.out.println("Request page with IP: " + document.text().trim());
            } catch (IOException e) {
                continue;
            }
        }
    }
    public static OkHttpClient httpClient = new OkHttpClient();

    public static OkHttpClient getHttpClientWithProxy(String proxy) {
        Proxy proxyObj = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.split(":")[0], Integer.parseInt(proxy.split(":")[1])));
        return new OkHttpClient.Builder().proxy(proxyObj).build();
    }

    public static OkHttpClient getHttpClientWithRandomProxy(List<String> proxies) {
        Random rand = new Random();
        String randomProxy = proxies.get(rand.nextInt(proxies.size()));
        return getHttpClientWithProxy(randomProxy);
    }

    public static OkHttpClient getSession(List<String> proxies) {
        return getHttpClientWithRandomProxy(proxies);
    }
}
