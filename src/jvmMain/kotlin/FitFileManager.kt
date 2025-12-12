import com.garmin.fit.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.File

class FitFileManager {
    
    fun changeDeviceType(inputPath: String, outputPath: String, newManufacturerId: Int, newProductId: Int) {
        val decode = Decode()
        val messages = mutableListOf<Mesg>()
        
        val mesgBroadcaster = MesgBroadcaster()
        mesgBroadcaster.addListener(MesgListener { mesg ->
            if (mesg.num == MesgNum.FILE_ID) {
                // Modify File ID message
                val fileIdMesg = FileIdMesg(mesg)
                fileIdMesg.manufacturer = newManufacturerId
                fileIdMesg.product = newProductId
                messages.add(fileIdMesg)
            } else {
                messages.add(mesg)
            }
        })

        val inputStream = FileInputStream(File(inputPath))
        try {
             // We use runCatching because sometimes decode check might fail on header if not strictly valid but we try anyway
             decode.read(inputStream, mesgBroadcaster, mesgBroadcaster) 
        } finally {
            inputStream.close()
        }

        // Write to new file
        val fileEncoder = FileEncoder(File(outputPath), Fit.ProtocolVersion.V2_0)
        try {
            fileEncoder.write(messages)
        } finally {
            fileEncoder.close()
        }
    }
}
