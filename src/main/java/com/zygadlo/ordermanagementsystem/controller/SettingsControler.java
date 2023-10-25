package com.zygadlo.ordermanagementsystem.controller;

import com.zygadlo.ordermanagementsystem.model.FileSettings;
import com.zygadlo.ordermanagementsystem.repository.FileStructureRepository;
import com.zygadlo.ordermanagementsystem.service.SettingsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("settings")
public class SettingsControler {

    private SettingsService settingsService;

    public SettingsControler(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    //TODO: make fileSettings html page
    @GetMapping("/")
    public String goToSettings(){
        return "fileSettings";
    }


    @PostMapping("/saveSettingsForFile")
    public String saveFileSettings(@RequestParam(name = "whatFile")String whatFile,
                                   @RequestParam(name = "separator")String separator,
                                   @RequestParam(name = "extension")String extension,
                                   @RequestParam(name = "eanOrder")Integer eanOrder,
                                   @RequestParam(name = "nameOrder")Integer nameOrder,
                                   @RequestParam(name = "amountOrder")Integer amountOrder,
                                   @RequestParam(name = "sallerCodeOrder")Integer sallerCodeOrder,
                                   @RequestParam(name = "priceOrder")Integer priceOrder,
                                   @RequestParam(name = "none")Integer none) {

        Map<Integer,String> fieldsOrder = settingsService.createFieldsOrderMap(eanOrder,nameOrder,amountOrder,sallerCodeOrder,priceOrder,none);

        if (settingsService.checkIfValidFieldsOrder(fieldsOrder,whatFile))
            settingsService.saveOrUpdateFileSettings(whatFile,extension,separator,fieldsOrder);
        else
            return "redirect:/?error=true";

        return "redirect:/#tab1";
    }
}
