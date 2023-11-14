package org.jasonf.boot;

import lombok.Data;
import org.jasonf.Constant;
import org.jasonf.config.RegistryConfig;
import org.jasonf.generator.IDGenerator;
import org.jasonf.generator.impl.SnowIDGenerator;
import org.jasonf.loadbalance.AbstractLoadBalancer;
import org.jasonf.loadbalance.impl.ConsistentHashLoadBalancer;
import org.jasonf.registry.Registry;
import org.jasonf.transfer.enumeration.CompressorType;
import org.jasonf.transfer.enumeration.SerializeType;

/**
 * @Author jasonf
 * @Date 2023/11/12
 * @Description
 */

@Data
public class Configuration {
    private String appName = "XQ";
    private String group = "default";
    private int port = 8102;

    private Registry registry = new RegistryConfig("zookeeper://123.60.86.242:2181").getRegistry();

    private SerializeType serialize = SerializeType.JDK;
    private CompressorType compress = CompressorType.GZIP;

    private IDGenerator idGenerator = new SnowIDGenerator(Constant.DATA_CENTER_ID, Constant.MACHINE_ID);
    private AbstractLoadBalancer loadBalancer = new ConsistentHashLoadBalancer(registry);

    public Configuration() {
        // SPI 自动发现机制
        ServiceLoader.load(this);

        // 解析 XML 配置文件
        XmlResolver.resolve(this);
    }
}
