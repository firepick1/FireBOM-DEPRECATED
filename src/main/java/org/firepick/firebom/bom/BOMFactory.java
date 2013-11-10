package org.firepick.firebom.bom;
/*
   BOMFactory.java
   Copyright (C) 2013 Karl Lew <karl@firepick.org>. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import net.sf.ehcache.CacheManager;
import org.firepick.firebom.part.PartFactory;
import org.firepick.relation.IRowVisitor;
import org.firepick.relation.RelationPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BOMFactory implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(BOMFactory.class);
    private final ConcurrentLinkedQueue<BOM> bomQueue = new ConcurrentLinkedQueue<BOM>();
    private OutputType outputType = OutputType.DEFAULT;
    private Thread worker;
    private PartFactory partFactory;
    private boolean workerPaused;
    private Lock backgroundLock = new ReentrantLock();

    public void shutdown() {
        logger.info("Shutting down Ehcache");
        CacheManager.getInstance().shutdown();
    }

    public BOM createBOM(URL url) {
        BOM bom = new BOM(url);
        synchronized (bomQueue) {
            bomQueue.add(bom);
            if (worker == null) {
                worker = new Thread(this);
                worker.start();
            }
        }
        return bom;
    }

    @Override
    public void run() {
        for (; ; ) {
            synchronized (bomQueue) {
                if (bomQueue.size() == 0) {
                    worker = null;
                    return;
                }
            }

            if (isWorkerPaused()) {
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e) {
                    logger.error("interrupted", e);
                }
            } else {
                try {
                    synchronized (bomQueue) {
                        BOM bom = bomQueue.poll();
                        if (bom != null) {
                            if (!bom.resolve(0)) {
                                logger.info("Requeing bom for resolve() {}", bom.getUrl());
                                bomQueue.add(bom);
                            }
                        }
                    }
                }
                catch (Exception e) {
                    logger.error("Could not resolve BOM", e);
                }
            }
        }
    }

    public BOMFactory printBOM(PrintStream printStream, BOM bom, IRowVisitor rowVisitor) {
        switch (outputType) {
            case MARKDOWN:
                new BOMMarkdownPrinter().print(bom, printStream, rowVisitor);
                break;
            case HTML:
                new BOMHtmlPrinter().setPrintHtmlWrapper(true).setTitle(bom.getTitle()).print(bom, printStream, rowVisitor);
                break;
            case HTML_TABLE:
                new BOMHtmlPrinter().setPrintHtmlWrapper(false).setTitle(bom.getTitle()).print(bom, printStream, rowVisitor);
                break;
            default:
            case CSV:
                new RelationPrinter().print(bom, printStream, rowVisitor);
                break;
        }

        return this;
    }

    public OutputType getOutputType() {
        return outputType;
    }

    public BOMFactory setOutputType(OutputType outputType) {
        this.outputType = outputType;
        return this;
    }

    public PartFactory getPartFactory() {
        if (partFactory == null) {
            setPartFactory(PartFactory.getInstance());
        }
        return partFactory;
    }

    public BOMFactory setPartFactory(PartFactory partFactory) {
        this.partFactory = partFactory;
        return this;
    }

    public boolean isWorkerPaused() {
        return workerPaused;
    }

    public BOMFactory setWorkerPaused(boolean workerPaused) {
        synchronized (bomQueue) {
            logger.info("setWorkerPaused({})", workerPaused);
            this.workerPaused = workerPaused;
        }
        return this;
    }

    public enum OutputType {
        DEFAULT,
        MARKDOWN,
        HTML,
        HTML_TABLE,
        CSV
    }

}
