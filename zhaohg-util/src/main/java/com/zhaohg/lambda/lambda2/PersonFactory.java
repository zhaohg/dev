package com.zhaohg.lambda.lambda2;

interface PersonFactory<P extends Person> {
    P create(String firstName, String lastName);
}