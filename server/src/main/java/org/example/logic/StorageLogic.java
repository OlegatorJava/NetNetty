package org.example.logic;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.example.model.Command;
import org.example.model.Message;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class StorageLogic {

     static Map<String,String> loginPass = new HashMap<>();

    public static void process(Message message, Channel channel) {

        if(message.getCommand().equals("AUTH") || message.getCommand() != null){
               try {
                   if (loginPass.containsKey(message.getNameUser())) {
                       if (loginPass.get(message.getNameUser()).equals(message.getPassword())) {
                           ChannelFuture future = channel.writeAndFlush(
                                   Message.builder()
                                           .command(Command.AUTH_OK.getCommand())
                                           .build()
                           );
                           future.addListener(ChannelFutureListener.CLOSE);
                       } else if (loginPass.get(message.getNameUser()) != null) {
                           ChannelFuture future = channel.writeAndFlush(
                                   Message.builder()
                                           .status("ERROR password is NULL")
                                           .build()
                           );
                           future.addListener(ChannelFutureListener.CLOSE);
                       } else {
                           ChannelFuture future = channel.writeAndFlush(
                                   Message.builder()
                                           .status("ERROR Invalid password")
                                           .build()
                           );
                           future.addListener(ChannelFutureListener.CLOSE);
                       }

                   } else {
                       loginPass.put(message.getNameUser(), message.getPassword());
                       ChannelFuture future = channel.writeAndFlush(
                               Message.builder()
                                       .command(Command.AUTH_OK.getCommand())
                                       .build()
                       );
                       future.addListener(ChannelFutureListener.CLOSE);
                   }
               }catch(NullPointerException e){
                   loginPass.put(message.getNameUser(), message.getPassword());
                   ChannelFuture future = channel.writeAndFlush(
                           Message.builder()
                                   .command(Command.AUTH_OK.getCommand())
                                   .build()
                   );
                   future.addListener(ChannelFutureListener.CLOSE);
            }
        }

        if (message.getCommand().equals("PUT")) {
            Path dir = Path.of("server", message.getNameUser());
            try {
                Files.createDirectories(dir);
                }catch(FileAlreadyExistsException ignore){}
                        catch (IOException e) {
                            ChannelFuture future = channel.writeAndFlush(
                                    Message.builder()
                                            .command(message.getCommand())
                                            .status("FILE ERROR")
                                            .build()
                            );
                            future.addListener(ChannelFutureListener.CLOSE);
                            return;
                        }
            Path file = Path.of("server", message.getNameUser(), message.getFile());
                try {
                    System.out.println("file = " + file);
                    Files.createFile(file);
                } catch (FileAlreadyExistsException ignored) {
                    System.out.println("Файл уже создан");
                } catch (IOException e) {
                    ChannelFuture future = channel.writeAndFlush(
                            Message.builder().command(message.getCommand()).status("FILE ERROR").build()
                    );
                    future.addListener(ChannelFutureListener.CLOSE);
                    return;
                }
            try (FileOutputStream output = new FileOutputStream(file.toFile())) {
                output.write(message.getData());
            } catch (IOException e) {
                ChannelFuture future = channel.writeAndFlush(
                        Message.builder().command(message.getCommand()).status("FILE ERROR").build()
                );
                future.addListener(ChannelFutureListener.CLOSE);
                return;
            } finally {
                channel.close();
            }
        }
        if (message.getCommand().equals("GET")) {
            Path file = Path.of("server", message.getFile());
            try {
                if (Files.exists(file) && Files.size(file) < 10_000) {
                    Message message1 = Message.builder()
                            .command(message.getCommand())
                            .file(file.getFileName().toString())
                            .status("OK")
                            .length(Files.size(file))
                            .data(Files.readAllBytes(file))
                            .build();
                    channel.writeAndFlush(message1);
                }
            } catch (IOException e) {
                ChannelFuture future = channel.writeAndFlush(
                        Message.builder().command(message.getCommand()).status("FILE ERROR").build()
                );
                future.addListener(ChannelFutureListener.CLOSE);
            } finally {
                channel.close();
            }
        }
        if (message.getCommand().equals("DELETE")) {
            Path file = Path.of("server", message.getNameUser(),message.getFile());
            try {
                if (Files.exists(file)) {
                    Files.delete(file);
                    Message message1 = Message.builder()
                            .command(message.getCommand())
                            .file(file.getFileName().toString())
                            .status("OK")
                            .length(Files.size(file))
                            .data(Files.readAllBytes(file))
                            .build();
                    channel.writeAndFlush(message1);
                }
            } catch (IOException e) {
                e.printStackTrace();
                ChannelFuture future = channel.writeAndFlush(
                        Message.builder()
                                .command(message.getCommand())
                                .status("FILE ERROR")
                                .build()
                );
                future.addListener(ChannelFutureListener.CLOSE);
            } finally {
                channel.close();
            }
        }
    }

}