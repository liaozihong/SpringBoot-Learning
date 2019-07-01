namespace java com.dashuai.learning.thrift.service
service RPCDateService{
    string getDate(1:string userName),

    bool postStudent(1:Student student),

    Student getStudent(1:i32 userId);


}
struct Student {
1: required i32 userId;
2: required string username;
3: required string text;
}
