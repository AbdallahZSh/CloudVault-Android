namespace CloudVault.Api.DTOs;
//ننشئ أول API حقيقي يحذف الملف من Cloudinary. بعد هذه الخطوات سيكون Android قادرًا على طلب الحذف من الـ Backend بدلًا من كشف الـ API Secret.
public class DeleteFileRequest
{
    public string PublicId { get; set; } = "";
}