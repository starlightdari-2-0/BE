package com.example.startlight.community.funeral.controller;

import com.example.startlight.community.funeral.dao.FuneralDao;
import com.example.startlight.community.funeral.entity.Funeral;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/funeral")
public class FuneralController {
    private final FuneralDao funeralDao;

    @PostMapping()
    public ResponseEntity<List<Funeral>> findByName(@RequestParam("name") String name) {
        List<Funeral> byName = funeralDao.findByName(name);
        return ResponseEntity.status(HttpStatus.OK).body(byName);
    }
}
