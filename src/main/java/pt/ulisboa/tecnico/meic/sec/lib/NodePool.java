package pt.ulisboa.tecnico.meic.sec.lib;

import pt.ulisboa.tecnico.meic.sec.lib.exception.EntityAlreadyExistsException;
import pt.ulisboa.tecnico.meic.sec.lib.exception.EntityNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NodePool {
    private static final int DEFAULT_INITIAL_PORT = 3001;
    private static final int DEFAULT_FINAL_PORT = 3004;

    private List<Node> nodes;

    public NodePool(){
        nodes = new ArrayList<>();
        for(int port = DEFAULT_INITIAL_PORT; port < DEFAULT_FINAL_PORT + 1; port++)
            nodes.add(new Node(port));
    }

    public NodePool(int[] ports){
        nodes = new ArrayList<>();
        for(int port: ports)
            nodes.add(new Node(port));
    }

    public NodePool(List<Node> nodes){
        this.nodes = nodes;
    }

    public int size() {
        return nodes.size();
    }

    public List<User> register(User user) throws IOException {
        ArrayList<Thread> threads = new ArrayList<>(size());
        ArrayList<User> usersResponses = new ArrayList<>(size());

        for (Node node: nodes) {
            Thread thread = new Thread(() -> {
                try {
                    usersResponses.add(node.register(user));
                } catch (IOException | EntityAlreadyExistsException ignored) {
                    // If a thread crashed, it's probably connection problems
                }
            });

            threads.add(thread);
            thread.start();
        }
        for (Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return usersResponses;
    }

    public List<Password> putPassword(Password password) throws IOException {
        ArrayList<Thread> threads = new ArrayList<>(size());
        ArrayList<Password> passwordResponses = new ArrayList<>(size());

        for (Node node: nodes) {
            Thread thread = new Thread(() -> {
                try {
                    passwordResponses.add(node.putPassword(password));
                } catch (IOException ignored) {
                    // If a thread crashed, it's probably connection problems
                } catch (EntityAlreadyExistsException e) {
                    passwordResponses.add((Password) e.getEntity());
                }
            });

            threads.add(thread);
            thread.start();
        }

        for (Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return passwordResponses;
    }

    public List<Password> retrievePassword(Password password) throws IOException {
        ArrayList<Thread> threads = new ArrayList<>(size());
        ArrayList<Password> passwordResponses = new ArrayList<>(size());

        for (Node node: nodes) {
            Thread thread = new Thread(() -> {
                try {
                    passwordResponses.add(node.retrievePassword(password));
                } catch (IOException ignored) {
                    // If a thread crashed, it's probably connection problems
                } catch (EntityNotFoundException e) {
                    passwordResponses.add(null);
                }
            });

            threads.add(thread);
            thread.start();
        }

        for (Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return passwordResponses;
    }



    public List<IV> putIV(IV iv) throws IOException {
        ArrayList<Thread> threads = new ArrayList<>(size());
        ArrayList<IV> IVResponses = new ArrayList<>(size());

        for (Node node: nodes) {
            Thread thread = new Thread(() -> {
                try {
                    IVResponses.add(node.putIV(iv));
                } catch (IOException ignored) {
                    // If a thread crashed, it's probably connection problems
                }
            });

            threads.add(thread);
            thread.start();
        }

        for (Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return IVResponses;
    }

    public List<IV> retrieveIV(IV iv) throws IOException {
        ArrayList<Thread> threads = new ArrayList<>(size());
        ArrayList<IV> IVResponses = new ArrayList<>(size());

        for (Node node: nodes) {
            Thread thread = new Thread(() -> {
                try {
                    IVResponses.add(node.retrieveIV(iv));
                } catch (IOException ignored) {
                    // If a thread crashed, it's probably connection problems
                } catch (EntityNotFoundException e) {
                    IVResponses.add(null);
                }
            });

            threads.add(thread);
            thread.start();
        }

        for (Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return IVResponses;
    }
}
