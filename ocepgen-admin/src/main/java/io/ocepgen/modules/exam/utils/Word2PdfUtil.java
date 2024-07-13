package io.ocepgen.modules.exam.utils;

import lombok.extern.slf4j.Slf4j;
import org.docx4j.Docx4J;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.org.apache.poi.util.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;

@Slf4j
public class Word2PdfUtil {

    public static void main(String[] args) throws Exception {
        String inputWord = "D:\\毕设\\OCEPGS\\output.docx";
        String outputFile = "D:\\毕设\\OCEPGS\\output.pdf";
        boolean isSuccess = Word2PdfUtil.convertDoc2Pdf(inputWord, outputFile);
        System.out.println("convertDoc2Pdf：============" + isSuccess);
    }

    /**
     * docx文档转换为PDF
     *
     * @param docPath WORD文档存储路径
     * @param pdfPath PDF文档存储路径
     * @throws Exception 可能为Docx4JException, FileNotFoundException, IOException等
     */
    public static boolean convertDoc2Pdf(String docPath, String pdfPath) throws Exception {
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File(docPath);
            fileOutputStream = new FileOutputStream(new File(pdfPath));
            WordprocessingMLPackage mlPackage = WordprocessingMLPackage.load(file);
            setFontMapper(mlPackage);
            Docx4J.toPDF(mlPackage, Files.newOutputStream(new File(pdfPath).toPath()));
            log.info("WORD文档转换为PDF成功===========");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("WORD文档转换为PDF失败===========");
        } finally {
            IOUtils.closeQuietly(fileOutputStream);
//            // 删除临时文件
//            File file = new File(docPath);
//            if (file.exists()) {
//                boolean isDelete = file.delete();
//                log.info("临时文件删除结果：{}", isDelete);
//            }
        }
        return false;
    }

    public static boolean convertDoc2PdfAndSend(String wordFilePath, HttpServletResponse response) {
        try {
            File word = new File(wordFilePath);
            // 加载Word文档
            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.load(word);

            // 设置响应的内容类型为PDF
            response.setContentType("application/pdf");
            // 设置Content-Disposition
            response.setHeader("Content-Disposition"
                    , "attachment; filename="+ word.getName().substring(0, word.getName().lastIndexOf('.')) + ".pdf");

            // 获取响应的输出流
            OutputStream out = response.getOutputStream();

            // 将Word文档转换为PDF并写入到响应的输出流中
            Docx4J.toPDF(wordPackage, out);
            out.flush(); // 确保所有内容都被写出
            out.close();
            log.info("WORD文档转换为PDF并发送成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("WORD文档转换为PDF失败");
            return false;
        } finally {
            // 删除临时文件
            File file = new File(wordFilePath);
            if (file.exists()) {
                // 临时上级目录
                File parentDir = file.getParentFile();

                // 删除临时文件
                boolean isDelete = file.delete();
                log.info("临时文件删除结果：{}", isDelete);
                // 删除临时上级目录
                if (parentDir.exists()) {
                    boolean isParentDirDelete = parentDir.delete();
                    log.info("临时上级目录删除结果：{}", isParentDirDelete);
                }
            }
        }
    }

    private static void setFontMapper(WordprocessingMLPackage mlPackage) throws Exception {
        Mapper fontMapper = new IdentityPlusMapper();
        // 加载字体文件（解决linux环境下无中文字体问题）
        if (PhysicalFonts.get("SimSun") == null) {
            System.out.println("加载本地SimSun字体库");
//          PhysicalFonts.addPhysicalFonts("SimSun", WordUtils.class.getResource("/fonts/SIMSUN.TTC"));
        }
        fontMapper.put("隶书", PhysicalFonts.get("LiSu"));
        fontMapper.put("宋体", PhysicalFonts.get("SimSun"));
        fontMapper.put("微软雅黑", PhysicalFonts.get("Microsoft Yahei"));
        fontMapper.put("黑体", PhysicalFonts.get("SimHei"));
        fontMapper.put("楷体", PhysicalFonts.get("KaiTi"));
        fontMapper.put("新宋体", PhysicalFonts.get("NSimSun"));
        fontMapper.put("华文行楷", PhysicalFonts.get("STXingkai"));
        fontMapper.put("华文仿宋", PhysicalFonts.get("STFangsong"));
        fontMapper.put("仿宋", PhysicalFonts.get("FangSong"));
        fontMapper.put("幼圆", PhysicalFonts.get("YouYuan"));
        fontMapper.put("华文宋体", PhysicalFonts.get("STSong"));
        fontMapper.put("华文中宋", PhysicalFonts.get("STZhongsong"));
        fontMapper.put("等线", PhysicalFonts.get("SimSun"));
        fontMapper.put("等线 Light", PhysicalFonts.get("SimSun"));
        fontMapper.put("华文琥珀", PhysicalFonts.get("STHupo"));
        fontMapper.put("华文隶书", PhysicalFonts.get("STLiti"));
        fontMapper.put("华文新魏", PhysicalFonts.get("STXinwei"));
        fontMapper.put("华文彩云", PhysicalFonts.get("STCaiyun"));
        fontMapper.put("方正姚体", PhysicalFonts.get("FZYaoti"));
        fontMapper.put("方正舒体", PhysicalFonts.get("FZShuTi"));
        fontMapper.put("华文细黑", PhysicalFonts.get("STXihei"));
        fontMapper.put("宋体扩展", PhysicalFonts.get("simsun-extB"));
        fontMapper.put("仿宋_GB2312", PhysicalFonts.get("FangSong_GB2312"));
        fontMapper.put("新細明體", PhysicalFonts.get("SimSun"));
        // Times New Roman
        fontMapper.put("Times New Roman", PhysicalFonts.get("TimesNewRomanPS"));
        // 解决宋体（正文）和宋体（标题）的乱码问题
        PhysicalFonts.put("PMingLiU", PhysicalFonts.get("SimSun"));
        PhysicalFonts.put("新細明體", PhysicalFonts.get("SimSun"));


        // 宋体&新宋体
        PhysicalFont simsunFont = PhysicalFonts.get("SimSun");
        fontMapper.put("SimSun", simsunFont);
        // 设置字体
        mlPackage.setFontMapper(fontMapper);
    }

}
