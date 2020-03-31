
package cd.go.common.archive;

import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

/**
 * The POSIX file permission mask.
 */
class PosixPerms {

  private static final int OWNER_READ   = 0400;
  private static final int OWNER_WRITE  = 0200;
  private static final int OWNER_EXEC   = 0100;

  private static final int GROUP_READ   = 0040;
  private static final int GROUP_WRITE  = 0020;
  private static final int GROUP_EXEC   = 0010;

  private static final int OTHERS_READ  = 0004;
  private static final int OTHERS_WRITE = 0002;
  private static final int OTHERS_EXEC  = 0001;

  private PosixPerms() {}

  /**
   * Converts a set of {@link PosixFilePermission} to chmod-style octal file mode.
   */
  public static int toOctalFileMode(Set<PosixFilePermission> permissions) {
    int result = 0;
    for (PosixFilePermission permissionBit : permissions) {
      switch (permissionBit) {
        case OWNER_READ:
          result |= OWNER_READ;
          break;
        case OWNER_WRITE:
          result |= OWNER_WRITE;
          break;
        case OWNER_EXECUTE:
          result |= OWNER_EXEC;
          break;
        case GROUP_READ:
          result |= GROUP_READ;
          break;
        case GROUP_WRITE:
          result |= GROUP_WRITE;
          break;
        case GROUP_EXECUTE:
          result |= GROUP_EXEC;
          break;
        case OTHERS_READ:
          result |= OTHERS_READ;
          break;
        case OTHERS_WRITE:
          result |= OTHERS_WRITE;
          break;
        case OTHERS_EXECUTE:
          result |= OTHERS_EXEC;
          break;
      }
    }
    return result;
  }

  public static boolean isExecuteable(int mode) {
    return (mode & OWNER_EXEC) > 0 || (mode & GROUP_EXEC) > 0 || (mode & OTHERS_EXEC) > 0;
  }
}