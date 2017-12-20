import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by gaojian on 2017/10/23.
 */
public class FileDownload {

    /**
     * 从网络Url中下载文件
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static void  downLoadFromUrl(String urlStr,String fileName,String savePath) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为5秒
        conn.setConnectTimeout(5*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");


        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);

        //文件保存位置
        File saveDir = new File(savePath);
        if(!saveDir.exists()){
            saveDir.mkdir();
        }

        File file = new File(saveDir+File.separator+fileName);

        FileOutputStream fos = new FileOutputStream(file);

        fos.write(getData);

        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }

        System.out.println("info:"+url+" download success");

    }


    /**
     * 从数组中获取元素
     * @param path
     * @return
     */
    public static ArrayList<String> getFilePath(String path) throws Exception{
        //首先读取文件
        ArrayList<String> list = new ArrayList<>();
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String temp;
        while((temp=br.readLine())!=null){
//            System.out.println(temp);
            list.add(temp);
        }
        br.close();
        return list;

    }


   /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        ExecutorService service = Executors.newFixedThreadPool(5);
        try{

            final ArrayList<String> arrayList = getFilePath("");

            int len  =arrayList.size();
            for(int i =0;i<len;i++){
                final int j = i;
                service.execute(new Runnable() {
                    public void run() {
                        try {
                            String temp = "http://www.sedar.com/GetFile.do?lang=EN&docClass=5&issuerNo=00030983&issuerType=03&projectNo=02031378&docId=3291822";
//                            String temp = arrayList.get(j);
//                            Integer t = temp.lastIndexOf("\\");
//                            System.out.println(t);
//                            String fileName = temp.replaceAll("http://pg.jrj.com.cn/acc/Res\"","").substring(t+1,temp.length());
                            String fileName = temp.replaceAll("http://pg.jrj.com.cn/acc/Res\\\\","").replaceAll("\\\\","-");

                            downLoadFromUrl(temp,fileName.trim(),"C:/Users/gaojian/Desktop/fileDownload/");

                            System.out.println("已经抓取"+j+"条");

                        } catch (Exception e) {
                            System.out.println("资源已经不存在！");
                        }
                    }
                });
                if (j % 5 == 0)
                    Thread.sleep(5000);
            }
        }catch (Exception e) {
          e.printStackTrace();
        }
        service.shutdown();
        long end = System.currentTimeMillis();

        System.out.println((end-start)/1000+"s");
    }

}
