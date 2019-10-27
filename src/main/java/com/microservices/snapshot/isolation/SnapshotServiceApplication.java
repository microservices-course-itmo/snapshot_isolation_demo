/*
 * Copyright (c) 2019 Dell Technologies
 * All Rights Reserved
 * This software contains the intellectual property of Dell Technologies or is
 * licensed to Dell Technologies from third parties. Use of this software and the
 * intellectual property contained therein is expressly limited to the terms and
 * conditions of the License Agreement under which it is provided by or on behalf
 * of Dell Technologies.
 */

package com.microservices.snapshot.isolation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class SnapshotServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnapshotServiceApplication.class, args);
    }
}
