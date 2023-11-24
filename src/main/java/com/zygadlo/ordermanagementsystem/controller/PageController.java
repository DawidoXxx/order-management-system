package com.zygadlo.ordermanagementsystem.controller;
import com.zygadlo.ordermanagementsystem.constans.NamesConstans;
import com.zygadlo.ordermanagementsystem.model.Product;
import com.zygadlo.ordermanagementsystem.model.ProductWithPricesToDisplay;
import com.zygadlo.ordermanagementsystem.model.Seller;
import com.zygadlo.ordermanagementsystem.repository.DataBaseUpdateInfo;
import com.zygadlo.ordermanagementsystem.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
public class PageController {

    @Value("${upload.database}")
    private String databasePath;
    @Value("${create.ordersToSend}")
    private String ordersToSendPath;

    private final ConfigService configService;
    private final ShopService shopService;
    private final SellerService sellerService;
    private final ProductService productService;
    private final StorageService storageService;
    private final OrderService orderService;
    private final FindProductService findProductService;
    private final DataBaseUpdateInfo dataBaseUpdateInfo;

    public PageController(ConfigService configService,ShopService shopService,ProductService productService,
                          StorageService storageService,SellerService sellerService,OrderService orderService,
                          FindProductService findProductService, DataBaseUpdateInfo dataBaseUpdateInfo) {

        this.configService = configService;
        this.shopService = shopService;
        this.productService = productService;
        this.storageService = storageService;
        this.sellerService = sellerService;
        this.orderService = orderService;
        this.findProductService = findProductService;
        this.dataBaseUpdateInfo = dataBaseUpdateInfo;

        if(configService.checkIfSellerConfigFileExist("SellersConfig.txt")){
            configService.saveSellersToMongoDB("SellersConfig.txt");
        }
        if (sellerService.findAllSellers().isEmpty()){
            sellerService.saveSeller(new Seller(NamesConstans.SPI,1,".csv"));
            sellerService.saveSeller(new Seller(NamesConstans.MAS,1,".csv"));
            sellerService.saveSeller(new Seller(NamesConstans.WAB,1,".csv"));
            sellerService.saveSeller(new Seller(NamesConstans.KAM,1,".csv"));
            sellerService.saveSeller(new Seller(NamesConstans.LEM,1,".xlsx"));
            sellerService.saveSeller(new Seller(NamesConstans.STA,1,".csv"));
        }
    }


    @GetMapping("/")
    public String getMainPage(Model model,HttpServletRequest request){

        if (request.getSession().getAttribute(NamesConstans.IS_ORDER_FILE_FRESH)!=null)
            model.addAttribute(NamesConstans.IS_ORDER_FILE_FRESH,request.getSession().getAttribute(NamesConstans.IS_ORDER_FILE_FRESH));
        else
            model.addAttribute(NamesConstans.IS_ORDER_FILE_FRESH,false);

        //TODO:check if there is ane file in order directory
        model.addAttribute("dataMods",storageService.getDatabaseModTime());
        model.addAttribute("updateTime",dataBaseUpdateInfo.findAll());
        model.addAttribute("size",dataBaseUpdateInfo.findAll().size());
        model.addAttribute("shopsList",sellerService.findAllSellers());

        if (shopService.checkIsThereAreShopsInDatabase())
            model.addAttribute("myShopList",shopService.findAllShops());
        else {
            return "noShopSite";
        }

        return "homePage";
    }

    //go to database files pick page
    @GetMapping("/#tab1")
    public String getHomePage1(Model model){
        return "redirect:/#tab1";
    }

    //go to order file pick page
    @GetMapping("/#tab2")
    public String getHomePage2(Model model){
        return "redirect:/#tab2";
    }

    //create order files
    @GetMapping("/#tab3")
    public String getHomePage3(Model model){
        return "redirect:/#tab3";
    }

    //go to file page
    @GetMapping("/#tab4")
    public String getHomePage4(Model model){
        return "redirect:/#tab4";
    }

    @GetMapping("/#tab5")
    public String getHomePage5(Model model) {
        return "redirect:/#tab5"; }

    //to search page
//    @GetMapping("/#tab6")
//    public String getPageToSearchProducts(Model model){
//        return "redirect:/#tab6";
//    }

    @GetMapping("/updateProductsFromSallers")
    public String updateProducts(){
        LocalDateTime now = LocalDateTime.now();
        productService.updateProducts(now);
        return "redirect:/#tab3";
    }

    @PostMapping(value = "/uploadDatabaseFile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadDatabaseFile(@RequestParam MultipartFile file, @RequestParam("sallersName") String name,
                                     @RequestParam("priorytet")String priority,
                                     @RequestParam("przelicznik") Optional<String> przelicznik,
                                     @RequestParam("clo")Optional<String> clo,
                                     @RequestParam("rabat")Optional<String> rabat){

        Seller seller = sellerService.findSellerByName(name);
        seller.setPriority(Integer.parseInt(priority));
        sellerService.saveSeller(seller);

        storageService.uploadFile(file,false,name+seller.getExtension());
        return "redirect:/#tab1";
    }

    //upload order file from user
    @PostMapping(value = "/uploadOrderFile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadOrderFile(@RequestParam MultipartFile file,
                                  //@RequestParam("zamowienie")String orderName,
                                  HttpServletRequest request){
        String name = "order.txt";
        storageService.uploadFile(file,true,name);
        request.getSession().setAttribute(NamesConstans.IS_ORDER_FILE_FRESH,true);
        return "redirect:/#tab2";
    }

    //This method gonna start methods in order service like:
    //-get data from order delivered by user
    //-get data of products, related to those from previous method, form mongodb
    //-sort prices in those products(one product object contains prices from all sellers)
    //-create and fill order file with all seller prices, seller names, amounts, product names
    //-create and fill order files containing data related only to one seller
    @PostMapping("/createOrder")
    public String createOrder(@RequestParam List<String> sellers,@RequestParam String shopName,HttpServletRequest request){
        List<Product> productList = orderService.loadDataFromOrderFile();
        orderService.loadDataFromDB(productList);
        orderService.getLowestPrices(productList);
        orderService.calculateAndSaveSavings(productList);
        String pathToCreatedOrderFiles = orderService.writeDataToOrderFile(sellers,shopName,productList);
        orderService.writeDataToOrderFileToDifferentSellers(sellers,shopName,productList);
        request.getSession().setAttribute(NamesConstans.IS_ORDER_FILE_FRESH,false);
        request.getSession().setAttribute(NamesConstans.ORDER_CREATED_FILES_PATH,pathToCreatedOrderFiles);
        return "redirect:/#tab4";
    }

    @GetMapping("/deleteDataBase/{name}")
    public String deleteDataBase(@PathVariable("name") String name){
        storageService.deleteFile(new File(databasePath),name);
        return "redirect:/#tab1";
    }

    @GetMapping("/noShopSite")
    public String noShopSite(){
        return "noShopSite";
    }

    @PostMapping("/findProductByEanOrName")
    public String findProductByEanOrName(@RequestParam(name = "isEan")String isEan, @RequestParam(name = "eanOrName") String userInput, RedirectAttributes redirectAttributes){

        List<ProductWithPricesToDisplay> list;

        if (isEan.equals("true")){
            list = findProductService.findProductsByEanRegex(userInput);
        }
        else{
            list = findProductService.findProductsByName(userInput);
        }

        redirectAttributes.addFlashAttribute("searchedProducts",list);

        return "redirect:/#tab5";
    }

    @GetMapping(value = "/zip-download",produces = "application/zip")
    public void zipDownload(HttpServletRequest request, HttpServletResponse response) throws IOException{
        ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
        String path = request.getSession().getAttribute(NamesConstans.ORDER_CREATED_FILES_PATH).toString();
        File createdOrderFilesDir;
        if (path!=null&&path.length()>0) {
            createdOrderFilesDir = new File(path);
            if (createdOrderFilesDir.listFiles() != null)
                for (File file : createdOrderFilesDir.listFiles()) {
                    FileSystemResource resource = new FileSystemResource(file);
                    ZipEntry zipEntry = new ZipEntry(resource.getFilename());
                    zipEntry.setSize(resource.contentLength());
                    zipOutputStream.putNextEntry(zipEntry);
                    StreamUtils.copy(resource.getInputStream(),zipOutputStream);
                    zipOutputStream.closeEntry();
                }
            zipOutputStream.finish();
            zipOutputStream.close();
            String zipFileName = "orders_to_send_"+orderService.getTodayDate()+".zip";
            response.setStatus(HttpServletResponse.SC_OK);
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentDispositionFormData("attachment",zipFileName+".zip");
//            response.addHeader(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+zipFileName+"\"");
            response.addHeader(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+zipFileName+"\"");
        }
    }
}
