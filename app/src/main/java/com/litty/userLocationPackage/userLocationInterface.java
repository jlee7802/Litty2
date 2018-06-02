package com.litty.userLocationPackage;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface userLocationInterface {
    @LambdaFunction
    String updateUserLocation(userLocation userLocationObj);
}
