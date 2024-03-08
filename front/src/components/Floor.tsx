import { Center } from "@chakra-ui/react";

function FloorBox({ children }: { children: React.ReactNode }) {
  return (
    <Center minH={10} h={10} minW={10} w={10} border={`1px solid #000`}>
      {children}
    </Center>
  );
}

export default FloorBox;
