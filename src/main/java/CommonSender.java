import exceptions.AppRuntimeException;

import java.io.*;
import java.net.*;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Created by bskrypka on 07.09.2017.
 */
public class CommonSender implements Runnable {
    private  FileOutputStream fos;
    private int threadCount;
    private static URL address;
    private ResourceBundle myBundle;

    public static void main(String[] args) {
        CommonSender sender = new CommonSender();
        sender.startSending(args);


    }


    private void startSending(String[] args) {
        try {
            if (prepare(args)) {
                System.out.println("Sended request to  " + address + " frequency= " + threadCount);
                for (int i = 0; i < threadCount; i++) {
                    Thread getReq = new Thread(new CommonSender());
                    getReq.start();
                }
            }else {
                System.out.println("Not configured");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        close();
    }

    public void close() {
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected boolean prepare(String[] args) {
        try {
            fos = new FileOutputStream("out.log", false);
            System.setOut(new PrintStream(fos));
            System.setErr(new PrintStream(fos));

          // external file
//            if (args == null || args.length == 0 || args[0].length() == 0) {
//                throw new AppRuntimeException("Не указан параметр - имя файла с настройками!");
//            }
//            File confFile = new File(args[0]);

            //internal file
            ClassLoader classLoader = getClass().getClassLoader();
            File confFile = new File(classLoader.getResource("conf.properties").getFile());


            if (!confFile.exists()) {
                throw new AppRuntimeException("Отсутствует файл настроек: " + args[0]);
            }
            InputStream inputStream = new FileInputStream(confFile);
            myBundle = new PropertyResourceBundle(new InputStreamReader(inputStream, "windows-1251"));
            inputStream.close();
            threadCount = Integer.valueOf(myBundle.getString("REQUEST_COUNT"));
            address = new URL(myBundle.getString("URL"));

            return true;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e1){
            System.out.println("Error while getting protocol " + e1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void go() {
        try {

            HttpURLConnection connection = (HttpURLConnection) address.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent",  "");

            int responseCode = connection.getResponseCode();
//            System.out.println("Sending 'GET' request to URL : " + address);
//            System.out.println("Response Code : " + responseCode);

            connection.disconnect();

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {
            go();
    }
}
