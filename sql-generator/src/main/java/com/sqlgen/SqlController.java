package com.sqlgen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class SqlController {

    @Autowired
    private SqlGeneratorService sqlGeneratorService;

    @PostMapping("/generate")
    public SqlResponse generateSql(@RequestBody SqlRequest request) {
        return sqlGeneratorService.generateSql(request.getPrompt());
    }
}