#import "RNIronSource.h"

@implementation RNIronSource {
    RCTResponseSenderBlock _requestRewardedVideoCallback;
}

#pragma mark - instance

+ (instancetype)sharedInstance {
    static RNIronSource *instance = nil;
    static dispatch_once_t onceToken = 0;
    dispatch_once(&onceToken, ^{
      if (instance == nil) {
          instance = [[RNIronSource alloc] init];
      }
    });

    return instance;
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE()

- (NSArray<NSString *> *)supportedEvents {
    return @[@"impressionDataDidSucceed"
             ];
}

// Initialize IronSource before showing the Rewarded Video
RCT_EXPORT_METHOD(initializeIronSource:(NSString *)appId
                  userId:(NSString *)userId
                  options:(NSDictionary *)options
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejector:(RCTPromiseRejectBlock)reject)
{
    NSLog(@"initializeIronSource called!! with key %@ and user id %@", appId, userId);
    [IronSource setUserId:userId];
    [IronSource addImpressionDataDelegate:self];
    [IronSource initWithAppKey:appId];

    BOOL validateIntegration = [RCTConvert BOOL:options[@"validateIntegration"]];
    if (validateIntegration) {
        [ISIntegrationHelper validateIntegration];
    }
    resolve(nil);
}

RCT_EXPORT_METHOD(addImpressionDataDelegate)
{
    [IronSource addImpressionDataDelegate:self];
}

RCT_EXPORT_METHOD(setConsent:(BOOL)consent)
{
    [IronSource setConsent:consent];
}

RCT_EXPORT_METHOD(setUserId:(NSString *)userId)
{
    [IronSource setUserId:userId];
}

RCT_EXPORT_METHOD(getAdvertiserId:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
{
    @try {
        resolve([IronSource advertiserId]);
    }
    @catch (NSException *exception) {
        resolve(nil);
    }
}

#pragma mark - ISImpressionDataDelegate

- (void)impressionDataDidSucceed:(ISImpressionData *)impressionData {
    NSNumber *revenue = impressionData.revenue;
    NSString *ad_network = impressionData.ad_network;
    NSDictionary *all_data = impressionData.all_data;
    
    NSDictionary *data = @{
              @"revenue": revenue,
              @"ad_network": ad_network,
              @"all_data": all_data
            };
    
    [self sendEventWithName:@"impressionDataDidSucceed" body:data];
}

@end
