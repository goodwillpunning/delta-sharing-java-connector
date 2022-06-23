package com.databricks.labs.delta.sharing.java;

import com.databricks.labs.delta.sharing.java.adaptor.DeltaSharingJsonProvider;
import io.delta.sharing.spark.DeltaSharingProfileProvider;
import io.delta.sharing.spark.DeltaSharingRestClient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Factory class for {@link DeltaSharing}. It provides different constructors
 * that might be appropriate in different situations.
 */
public final class DeltaSharingFactory {

  /**
   * Constructor.
   *
   * @param profileProvider An instance of {@link DeltaSharingProfileProvider}.
   * @param checkpointPath An path to a temporary checkpoint location.
   * @return An instance of {@link DeltaSharing} client.
   * @throws IOException Transitive due to the call to
   *         {@link Files#createTempDirectory(String, FileAttribute[])}.
   */
  public static DeltaSharing create(DeltaSharingProfileProvider profileProvider,
      Path checkpointPath) throws IOException {

    if (!Files.exists(checkpointPath)) {
      Files.createDirectory(checkpointPath);
    }
    Path tempDir = Files.createTempDirectory(checkpointPath, "delta_sharing");
    tempDir.toFile().deleteOnExit();
    DeltaSharing instance = new DeltaSharing(profileProvider,
        new DeltaSharingRestClient(profileProvider, 120, 4, false),
        checkpointPath, tempDir, new HashMap<>());
    return instance;
  }

  /**
   * Constructor.
   *
   * @param providerConf A valid JSON document corresponding to
   *        {@link DeltaSharingProfileProvider}.
   * @param checkpointLocation A string containing a path to be used as a
   *        checkpoint location.
   * @return An instance of {@link DeltaSharing} client.
   * @throws IOException Transitive due to the call to
   *         {@link Files#createDirectories(Path, FileAttribute[])}.
   */
  public static DeltaSharing create(String providerConf,
      String checkpointLocation) throws IOException {
    Path checkpointPath = Paths.get(checkpointLocation);
    if (!Files.exists(checkpointPath)) {
      Files.createDirectories(checkpointPath);
    }
    DeltaSharingProfileProvider profileProvider =
        new DeltaSharingJsonProvider(providerConf);
    return create(profileProvider, checkpointPath);
  }
}
