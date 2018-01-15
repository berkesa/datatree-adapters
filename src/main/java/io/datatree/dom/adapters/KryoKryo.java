package io.datatree.dom.adapters;

import java.io.ByteArrayOutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.UUID;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import io.datatree.dom.BASE64;
import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractAdapter;

/**
 * <b>KRYO BINARY ADAPTER</b><br>
 * <br>
 * Description: Kryo is a fast and efficient object graph serialization
 * framework for Java. The goals of the project are speed, efficiency, and an
 * easy to use API. The project is useful any time objects need to be persisted,
 * whether to a file, database, or over the network.<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/com.esotericsoftware/kryo<br>
 * compile group: 'com.esotericsoftware', name: 'kryo', version: '4.0.1'<br>
 * <br>
 * <b>Invoke serializer and deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputBytes, "kryo");<br>
 * byte[] outputBytes = node.toBytes("kryo");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(10)
public class KryoKryo extends AbstractAdapter {

	// --- OBJECT MAPPER INSTANCE ---

	public Kryo kryo = new Kryo();

	// --- CONSTRUCTOR ---

	public KryoKryo() {
		kryo.register(UUID.class, new Serializer<UUID>() {

			@Override
			public final void write(final Kryo kryo, final Output output, final UUID obj) {
				output.writeLong(obj.getMostSignificantBits());
				output.writeLong(obj.getLeastSignificantBits());
			}

			@Override
			public final UUID read(final Kryo kryo, final Input input, final Class<UUID> clazz) {
				return new UUID(input.readLong(), input.readLong());
			}
			
		});
		kryo.register(Inet4Address.class, new Serializer<Inet4Address>() {

			@Override
			public final void write(final Kryo kryo, final Output output, final Inet4Address obj) {
				output.writeString(obj.getCanonicalHostName());
			}

			@Override
			public final Inet4Address read(final Kryo kryo, final Input input, final Class<Inet4Address> clazz) {
				try {
					return (Inet4Address) Inet4Address.getByName(input.readString());					
				} catch (Exception e) {
					return null;
				}
			}
			
		});
		kryo.register(Inet6Address.class, new Serializer<Inet6Address>() {

			@Override
			public final void write(final Kryo kryo, final Output output, final Inet6Address obj) {
				output.writeString(obj.getCanonicalHostName());
			}

			@Override
			public final Inet6Address read(final Kryo kryo, final Input input, final Class<Inet6Address> clazz) {
				try {
					return (Inet6Address) Inet6Address.getByName(input.readString());					
				} catch (Exception e) {
					return null;
				}
			}
			
		});		
	}

	// --- NAME OF THE FORMAT ---

	@Override
	public String getFormat() {
		return "kryo";
	}

	// --- IMPLEMENTED WRITER METHODS ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return BASE64.encode(toBinary(value, meta, insertMeta));
	}

	public byte[] toBinary(Object value, Object meta, boolean insertMeta) {
		return toBinary(value, meta, insertMeta, (input) -> {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);
			Output out = new Output(buffer);
			kryo.writeClassAndObject(out, input);
			out.flush();
			return buffer.toByteArray();
		});
	}

	// --- IMPLEMENTED PARSER METHODS ---

	@Override
	public Object parse(String source) throws Exception {
		return parse(BASE64.decode(source));
	}

	public Object parse(byte[] source) throws Exception {
		return kryo.readClassAndObject(new Input(source));
	}

}
