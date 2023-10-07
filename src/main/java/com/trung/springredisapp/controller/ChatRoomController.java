package com.trung.springredisapp.controller;

@RestController
@RequestMapping(value = "/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatRoomController {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private RoomsRepository roomsRepository;

    @GetMapping(value = "user/{userId}")
    public ResponseEntity<List<Room>> getRooms(@PathVariable("userId") int userId) {

    }
    @GetMapping(value = "/{roomId}")
    public ResponseEntity<List<Message>> getRoomMessages(@PathVariable("roomId") Long roomId,
                                                            @RequestParam("offset") Integer offset,
                                                            @RequestParam("size"    ) Integer size ) {
        
    }

    private String[] getAllUserIds(String roomId){
        if( roomId.indexOf(':') ) {
            throw new RuntimeException("Canot get user id from wrong roomId input!!");
        }

        String[] allUserIds = roomId.split(":");

        if( allUserIds.length < 2 ) {
            throw new RuntimeException("Canot get user id from wrong roomId input!!");
        }

        return allUserIds;
    }

    private Room createChatRoom(String roomId){
        String[] allUserIds = getAllUserIds(roomId);

        List<String> allUserNames = ArrayList<String>();

        for(String userId : allUserIds) {
            User userFromId = usersRepository.getUserById(Integer.parseInt(userIds[1]));
            if ( Objects.isNull(userFromId) ) { // inexist user or private chat room
                return null;
            }

            allUserNames.add( userFromId.getUsername() );
        }
        return new Room(roomId, allUserNames); // temporary just 2 people
    }

    private Message messageDeserialize(String value){

    }
}