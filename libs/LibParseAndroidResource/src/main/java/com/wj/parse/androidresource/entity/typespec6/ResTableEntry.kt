
/**
 * https://android.googlesource.com/platform/frameworks/base/+/master/libs/androidfw/include/androidfw/ResourceTypes.h#1491
 * This is the beginning of information about an entry in the resource
 * table.  It holds the reference to the name of this entry, and is
 * immediately followed by one of:
 *   * A Res_value structure, if FLAG_COMPLEX is -not- set.
 *   * An array of ResTable_map structures, if FLAG_COMPLEX is set.
 *     These supply a set of name/value mappings of data.
 *
 *  struct ResTable_entry
 * {
 *   // Number of bytes in this structure.
 *   uint16_t size;
 *   enum {
 *       // If set, this is a complex entry, holding a set of name/value
 *       // mappings.  It is followed by an array of ResTable_map structures.
 *       FLAG_COMPLEX = 0x0001,
 *       // If set, this resource has been declared public, so libraries
 *       // are allowed to reference it.
 *       FLAG_PUBLIC = 0x0002,
 *       // If set, this is a weak resource and may be overriden by strong
 *       // resources of the same name/type. This is only useful during
 *       // linking with other resource tables.
 *       FLAG_WEAK = 0x0004,
 *   };
 *   uint16_t flags;
 *   
 *   // Reference into ResTable_package::keyStrings identifying this entry.
 *   struct ResStringPool_ref key;
 * };
 */
class ResTableEntry{
}
