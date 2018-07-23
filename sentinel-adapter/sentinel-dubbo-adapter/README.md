# Sentinel Dubbo Adapter

Sentinel Dubbo Adapter provides service consumer filter and provider filter
for [Dubbo](http://dubbo.io/) services. 

To use Sentinel Dubbo Adapter, you can simply add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-dubbo-adapter</artifactId>
    <version>x.y.z</version>
</dependency>
```

The Sentinel filters are **enabled by default**. If don't want the filters enabled,
you can manually disable them. For example:

```xml
<dubbo:consumer filter="-sentinel.dubbo.consumer.filter"/>

<dubbo:provider filter="-sentinel.dubbo.provider.filter"/>
```

For more details of Dubbo filter, see [here](https://dubbo.incubator.apache.org/#/docs/dev/impls/filter.md?lang=en-us).