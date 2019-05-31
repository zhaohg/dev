package com.zhaohg.lambda.lambda2;


@FunctionalInterface
interface Fun<F, T> {
    T run(F from);
}
